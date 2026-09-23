package com.example.inventory.service;

import com.example.inventory.dto.ReservationResponse;
import com.example.inventory.dto.StockUpdateEvent;
import com.example.inventory.model.Product;
import com.example.inventory.repository.ProductRepository;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Step 09: Reactive Inventory Service
 * 
 * Key Concepts:
 * 1. Atomic Redis Lua Script Execution: Executes stock check + decrement in a single atomic operation in Redis.
 * 2. Non-blocking Reactive Pipeline: Uses Project Reactor flatMap/map operators without thread blocking.
 * 3. Event-Driven Broadcasting: Pushes updated stock & dynamic prices to SSE clients immediately after reservation.
 * 4. Optimistic DB Synchronization: Syncs Mongo DB document while preserving @Version concurrency safety.
 */
@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final RedisScript<Long> reserveStockLuaScript;
    private final DynamicPricingService dynamicPricingService;
    private final InventoryEventBroadcaster eventBroadcaster;

    public InventoryService(ProductRepository productRepository,
                            @Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate<String, String> redisTemplate,
                            RedisScript<Long> reserveStockLuaScript,
                            DynamicPricingService dynamicPricingService,
                            InventoryEventBroadcaster eventBroadcaster) {
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
        this.reserveStockLuaScript = reserveStockLuaScript;
        this.dynamicPricingService = dynamicPricingService;
        this.eventBroadcaster = eventBroadcaster;
    }

    /**
     * Seeds a new product into MongoDB and initializes its stock in Redis.
     */
    public Mono<Product> createProduct(Product product) {
        if (product.getCurrentPrice() == null) {
            product.setCurrentPrice(product.getBasePrice());
        }
        if (product.getAvailableStock() == null) {
            product.setAvailableStock(product.getTotalStock());
        }

        return productRepository.save(product)
                .flatMap(savedProduct -> {
                    String stockKey = getStockRedisKey(savedProduct.getId());
                    return redisTemplate.opsForValue()
                            .set(stockKey, String.valueOf(savedProduct.getAvailableStock()))
                            .thenReturn(savedProduct);
                });
    }

    /**
     * Retrieves a product entity by ID.
     */
    public Mono<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    /**
     * Attempts atomic flash-sale stock reservation.
     * 
     * @param productId Target product ID
     * @param userId Authenticated user ID making reservation
     * @param quantity Quantity to reserve
     * @return Mono<ReservationResponse> containing success state & reservation token
     */
    public Mono<ReservationResponse> reserveStock(String productId, String userId, Integer quantity) {
        String stockKey = getStockRedisKey(productId);

        // Execute Atomic Lua Script in Redis
        return redisTemplate.execute(
                reserveStockLuaScript,
                Collections.singletonList(stockKey),
                Collections.singletonList(String.valueOf(quantity))
        ).next().flatMap(remainingStock -> {
            if (remainingStock == null || remainingStock < 0) {
                return Mono.just(ReservationResponse.builder()
                        .success(false)
                        .message("Insufficient stock available for reservation")
                        .build());
            }

            String token = UUID.randomUUID().toString();
            Instant expirationTime = Instant.now().plusSeconds(300); // 5 minute reservation window

            // Sync MongoDB & Broadcast Event
            return syncProductStockAndBroadcast(productId, remainingStock.intValue())
                    .thenReturn(ReservationResponse.builder()
                            .success(true)
                            .message("Stock reserved successfully")
                            .reservationToken(token)
                            .reservedQuantity(quantity)
                            .expirationTime(expirationTime)
                            .build());
        });
    }

    private Mono<Product> syncProductStockAndBroadcast(String productId, int remainingStock) {
        return productRepository.findById(productId)
                .flatMap(product -> dynamicPricingService.calculateDynamicPrice(product, remainingStock)
                        .flatMap(newPrice -> {
                            product.setAvailableStock(remainingStock);
                            product.setCurrentPrice(newPrice);
                            return productRepository.save(product);
                        }))
                .doOnSuccess(updatedProduct -> {
                    if (updatedProduct != null) {
                        StockUpdateEvent event = StockUpdateEvent.builder()
                                .productId(updatedProduct.getId())
                                .currentPrice(updatedProduct.getCurrentPrice())
                                .remainingStock(updatedProduct.getAvailableStock())
                                .timestamp(Instant.now())
                                .build();
                        eventBroadcaster.publishEvent(event);
                    }
                });
    }

    private String getStockRedisKey(String productId) {
        return "product:stock:" + productId;
    }
}
