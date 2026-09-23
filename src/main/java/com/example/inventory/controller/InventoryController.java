package com.example.inventory.controller;

import com.example.auth.dto.response.ApiResponse;
import com.example.inventory.dto.ReservationRequest;
import com.example.inventory.dto.ReservationResponse;
import com.example.inventory.dto.StockUpdateEvent;
import com.example.inventory.model.Product;
import com.example.inventory.service.InventoryEventBroadcaster;
import com.example.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * Step 10: Inventory WebFlux REST & SSE Controller
 * 
 * Key Concepts:
 * 1. Server-Sent Events (SSE): GET /stream returns MediaType.TEXT_EVENT_STREAM_VALUE allowing browsers
 *    to receive real-time updates directly via reactive Flux streams without polling.
 * 2. Non-blocking Reactive Handlers: All controller endpoints return Mono<ResponseEntity<T>> or Flux<T>.
 * 3. Validation (@Valid): Automatically validates request bodies using Jakarta Bean Validation.
 */
@RestController
@RequestMapping("/api/v1/products")
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryEventBroadcaster eventBroadcaster;

    public InventoryController(InventoryService inventoryService,
                               InventoryEventBroadcaster eventBroadcaster) {
        this.inventoryService = inventoryService;
        this.eventBroadcaster = eventBroadcaster;
    }

    /**
     * Seeds/Creates a new product entity in MongoDB and initializes stock in Redis.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<ApiResponse<Product>>> createProduct(@RequestBody Product product) {
        return inventoryService.createProduct(product)
                .map(createdProduct -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success("Product created successfully", createdProduct)));
    }

    /**
     * Fetches details for a single product by ID.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Product>>> getProductById(@PathVariable String id) {
        return inventoryService.getProductById(id)
                .map(product -> ResponseEntity.ok(ApiResponse.success("Product fetched successfully", product)))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Product not found", 404, "Product with id " + id + " not found", null)));
    }

    /**
     * Reserves stock for a flash-sale item using atomic Redis Lua scripts.
     */
    @PostMapping("/reserve")
    public Mono<ResponseEntity<ApiResponse<ReservationResponse>>> reserveStock(
            @Valid @RequestBody ReservationRequest request,
            Mono<Principal> principalMono) {

        return principalMono
                .map(Principal::getName)
                .defaultIfEmpty("ANONYMOUS_USER")
                .flatMap(userId -> inventoryService.reserveStock(request.getProductId(), userId, request.getQuantity()))
                .map(response -> {
                    if (response.isSuccess()) {
                        return ResponseEntity.ok(ApiResponse.success("Stock reserved successfully", response));
                    } else {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ApiResponse.error(response.getMessage(), 409, response.getMessage(), response));
                    }
                });
    }

    /**
     * Live Stream Endpoint: Pushes real-time stock and dynamic price updates to client browser via SSE.
     */
    @GetMapping(value = "/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StockUpdateEvent>> streamProductUpdates(@PathVariable String id) {
        return eventBroadcaster.getEventStreamForProduct(id)
                .map(event -> ServerSentEvent.<StockUpdateEvent>builder()
                        .id(event.getProductId() + "-" + event.getTimestamp().toEpochMilli())
                        .event("stock-update")
                        .data(event)
                        .build());
    }
}
