package com.example.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Step 01: Product Entity (MongoDB Document)
 * 
 * Key Concepts:
 * 1. @Document: Maps this class to the "products" collection in MongoDB.
 * 2. @Version: Enables OPTIMISTIC LOCKING. If two threads try to update the database
 *    concurrently, MongoDB checks the version number. If versions mismatch, an 
 *    OptimisticLockingFailureException is thrown to prevent lost updates!
 * 3. BigDecimal: Used for monetary values (basePrice, currentPrice) to avoid double precision floating point errors.
 */
@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    private String id;

    private String name;

    private String description;

    private BigDecimal basePrice;

    private BigDecimal currentPrice;

    private Integer totalStock;

    private Integer availableStock;

    /**
     * Optimistic Locking Version field.
     * Prevents race conditions during database synchronization.
     */
    @Version
    private Long version;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
