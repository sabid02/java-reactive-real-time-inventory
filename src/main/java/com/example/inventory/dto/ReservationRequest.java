package com.example.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 03: ReservationRequest DTO
 * 
 * Key Concepts:
 * 1. Request Payload: Sent by authenticated users attempting to reserve stock during high-concurrency flash sales.
 * 2. Jakarta Validation (@NotBlank, @NotNull, @Min): Ensures invalid input is rejected at the API boundary
 *    before reaching reactive pipeline execution or database locks.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequest {

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
