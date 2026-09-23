package com.example.inventory.service;

import com.example.inventory.model.Product;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Step 07: Dynamic Pricing Service
 * 
 * Key Concepts:
 * 1. Reactive Business Service: Computes product price dynamic surges non-blockingly.
 * 2. Demand Velocity / Scarcity Surge Algorithm:
 *    - Stock < 10%: +50% Price Surge
 *    - Stock < 25%: +25% Price Surge
 *    - Stock < 50%: +10% Price Surge
 *    - Stock >= 50%: Base Price (1.0x)
 * 3. BigDecimal Precision: Prevents floating point rounding errors in financial pricing calculations.
 */
@Service
public class DynamicPricingService {

    /**
     * Calculates the dynamic price reactively based on base price and remaining available stock ratio.
     * 
     * @param product The target product entity
     * @param availableStock Current remaining stock balance
     * @return Mono emitting the calculated dynamic price
     */
    public Mono<BigDecimal> calculateDynamicPrice(Product product, Integer availableStock) {
        return Mono.fromCallable(() -> {
            if (product.getTotalStock() == null || product.getTotalStock() <= 0 || availableStock == null || availableStock <= 0) {
                return product.getBasePrice();
            }

            double stockRatio = (double) availableStock / product.getTotalStock();
            BigDecimal multiplier;

            if (stockRatio < 0.10) {
                multiplier = new BigDecimal("1.50"); // 50% Surge
            } else if (stockRatio < 0.25) {
                multiplier = new BigDecimal("1.25"); // 25% Surge
            } else if (stockRatio < 0.50) {
                multiplier = new BigDecimal("1.10"); // 10% Surge
            } else {
                multiplier = new BigDecimal("1.00"); // Base Price
            }

            return product.getBasePrice()
                    .multiply(multiplier)
                    .setScale(2, RoundingMode.HALF_UP);
        });
    }
}
