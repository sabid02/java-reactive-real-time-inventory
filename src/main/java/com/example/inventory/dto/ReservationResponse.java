package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Step 04: ReservationResponse DTO
 * 
 * Key Concepts:
 * 1. API Response: Returned to authenticated users after a flash-sale reservation attempt.
 * 2. Reservation Token: A unique token (e.g. UUID) that holds the user's temporary claim on stock.
 * 3. Expiration Time: Tells the client how long they have to complete checkout before stock is auto-released.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private boolean success;
    private String message;
    private String reservationToken;
    private Integer reservedQuantity;
    private Instant expirationTime;
}
