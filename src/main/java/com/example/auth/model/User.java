package com.example.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * MongoDB Document representing the "users" collection.
 * Demonstrates all major Java data types mapped to MongoDB BSON types.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    /**
     * Primary Key in MongoDB (String representation of BSON ObjectId).
     */
    @Id
    private String id;

    /**
     * Unique login handle (String).
     */
    @Indexed(unique = true)
    private String username;

    /**
     * Unique email address (String).
     */
    @Indexed(unique = true)
    private String email;

    /**
     * BCrypt hashed password (String).
     */
    private String password;

    /**
     * User's age (Primitive int -> 32-bit Integer in BSON).
     */
    private int age;

    /**
     * Wallet balance or account credits (Primitive double -> 64-bit Float in BSON).
     */
    private double walletBalance;

    /**
     * Total number of successful logins (Primitive long -> 64-bit Integer in BSON).
     */
    private long loginCount;

    /**
     * Account active status (Primitive boolean -> BSON Boolean).
     */
    @Builder.Default
    private boolean enabled = true;

    /**
     * Set of authorization roles (Set of Enum -> BSON Array of Strings).
     */
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    /**
     * Dynamic user settings/preferences (Map -> BSON Document/Object).
     */
    @Builder.Default
    private Map<String, String> preferences = new HashMap<>();

    /**
     * Creation timestamp (Instant -> BSON UTC DateTime).
     */
    @CreatedDate
    private Instant createdAt;

    /**
     * Last modified timestamp (Instant -> BSON UTC DateTime).
     */
    @LastModifiedDate
    private Instant updatedAt;
}
