package com.example.auth.repository;

import com.example.auth.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Reactive Mongo Repository interface for User document operations.
 * Provides non-blocking CRUD and custom query operations emitting Mono/Flux.
 */
@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

    /**
     * Finds a User by username asynchronously.
     * @param username User's unique login username
     * @return Mono emitting the User if found, or Mono.empty() if not.
     */
    Mono<User> findByUsername(String username);

    /**
     * Finds a User by email address asynchronously.
     * @param email User's email address
     * @return Mono emitting the User if found, or Mono.empty() if not.
     */
    Mono<User> findByEmail(String email);

    /**
     * Checks if a username already exists in MongoDB.
     * @param username User's username
     * @return Mono emitting true if username exists, false otherwise.
     */
    Mono<Boolean> existsByUsername(String username);

    /**
     * Checks if an email address already exists in MongoDB.
     * @param email User's email address
     * @return Mono emitting true if email exists, false otherwise.
     */
    Mono<Boolean> existsByEmail(String email);
}
