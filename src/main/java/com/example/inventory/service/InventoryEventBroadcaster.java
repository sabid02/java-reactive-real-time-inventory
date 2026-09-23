package com.example.inventory.service;

import com.example.inventory.dto.StockUpdateEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Step 08: Inventory Event Broadcaster Service
 * 
 * Key Concepts:
 * 1. Sinks.Many: Project Reactor construct used to programmatically push signals (events) to multiple subscribers.
 * 2. Multicast & Backpressure Buffer: Allows multiple connected web clients (SSE) to receive the same event stream
 *    while buffering events if network traffic slows down.
 * 3. Reactive Filtering: Exposes a Flux filtered by productId so clients only receive updates for products they are viewing.
 */
@Service
public class InventoryEventBroadcaster {

    private final Sinks.Many<StockUpdateEvent> sink;

    public InventoryEventBroadcaster() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    /**
     * Publishes a stock/price change event to all active SSE client streams.
     * 
     * @param event The updated stock & price event payload
     */
    public void publishEvent(StockUpdateEvent event) {
        sink.tryEmitNext(event);
    }

    /**
     * Retrieves the global reactive stream of all stock update events.
     */
    public Flux<StockUpdateEvent> getEventStream() {
        return sink.asFlux();
    }

    /**
     * Retrieves a reactive stream filtered for a specific product ID.
     * 
     * @param productId The ID of the product to filter events for
     */
    public Flux<StockUpdateEvent> getEventStreamForProduct(String productId) {
        return sink.asFlux()
                .filter(event -> event.getProductId().equals(productId));
    }
}
