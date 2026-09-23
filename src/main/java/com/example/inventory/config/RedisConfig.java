package com.example.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.context.annotation.Primary;

/**
 * Step 05: Reactive Redis Configuration & Atomic Lua Script Loader
 * 
 * Key Concepts:
 * 1. ReactiveRedisTemplate: Non-blocking Redis operations supporting Mono &
 * Flux.
 * 2. Atomic Lua Script: Executed directly on the Redis server thread to check
 * stock availability
 * and decrement atomically in a single operation, eliminating race conditions &
 * over-selling!
 */
@Configuration
public class RedisConfig {

    /**
     * Configures ReactiveRedisTemplate with String key and value serialization.
     */
    @Bean
    @Primary
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer serializer = new StringRedisSerializer();
        RedisSerializationContext<String, String> serializationContext = RedisSerializationContext
                .<String, String>newSerializationContext(serializer)
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

    /**
     * Defines the Atomic Stock Decrement Lua Script.
     * 
     * KEYS[1]: Redis stock key for the product (e.g., "product:stock:123")
     * ARGV[1]: Requested reservation quantity
     * 
     * Returns:
     * >= 0: Remaining stock after successful reservation
     * -1 : Insufficient stock (Reservation Failed)
     */
    @Bean
    public RedisScript<Long> reserveStockLuaScript() {
        String luaScript = """
                    local stockKey = KEYS[1]
                    local requestedQty = tonumber(ARGV[1])
                    local currentStock = tonumber(redis.call('get', stockKey) or '0')

                    if currentStock >= requestedQty then
                        local newStock = currentStock - requestedQty
                        redis.call('set', stockKey, newStock)
                        return newStock
                    else
                        return -1
                    end
                """;

        return RedisScript.of(luaScript, Long.class);
    }
}
