package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Step 02: StockUpdateEvent DTO
 * 
 * Key Concepts:
 * 1. Event Payload: Pushed via Server-Sent Events (SSE) to connected clients whenever price or inventory changes.
 * 2. Real-Time Data Transfer: Captures the updated state (productId, currentPrice, remainingStock, timestamp)
 *    so connected frontends can update UI live without polling.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUpdateEvent {

    private String productId;
    private BigDecimal currentPrice;
    private Integer remainingStock;
    private Instant timestamp;
}
