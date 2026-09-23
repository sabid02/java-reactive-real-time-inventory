package com.example.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

/**
 * Main entry point for the Reactive Spring Boot Service.
 */
@SpringBootApplication(scanBasePackages = "com.example")
@EnableReactiveMongoRepositories(basePackages = {"com.example.auth.repository", "com.example.inventory.repository"})
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    /**
     * Connection Test Runner:
     * Executes upon application startup to verify the connection to MongoDB Atlas.
     */
    @Bean
    public CommandLineRunner testMongoConnection(ReactiveMongoTemplate reactiveMongoTemplate) {
        return args -> {
            reactiveMongoTemplate.executeCommand("{ ping: 1 }")
                .retry(3)
                .subscribe(
                    result -> System.out.println("\n✅ ============================================\n🟢 SUCCESS: Connected to MongoDB Atlas!\nDocument: " + result.toJson() + "\n============================================\n"),
                    error -> System.err.println("\n❌ ============================================\n🔴 ERROR: Failed to connect to MongoDB Atlas!\nReason: " + error.getMessage() + "\n============================================\n")
                );
        };
    }
}
