package com.example.inventory.repository;

import com.example.inventory.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Step 06: Reactive MongoDB Product Repository
 * 
 * Key Concepts:
 * 1. ReactiveMongoRepository: Extends Spring Data Reactive Mongo to perform non-blocking DB operations.
 * 2. Non-blocking Return Types: Queries return Mono<Product> or Flux<Product> instead of plain domain objects,
 *    preventing database IO from blocking WebFlux event loops.
 */
@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

    /**
     * Custom reactive query method to find a product by exact name.
     */
    Mono<Product> findByName(String name);
}
