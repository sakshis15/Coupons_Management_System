package com.ecommerce.coupons.model;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiscountResult {
    private Double discountAmount;
    private String message;
    
    public DiscountResult() {
        this.discountAmount = 0.0;
        this.message = "No discount applied";
    }
}