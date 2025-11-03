package com.ecommerce.coupons.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("BXGY")
public class BxGyCoupon extends Coupon {
    @ElementCollection
    private List<String> buyProductIds;
    
    @ElementCollection
    private List<String> getProductIds;
    
    private Integer buyQuantity;
    private Integer getQuantity;
    private Integer repetitionLimit;
    
    @Override
    public boolean isApplicable(Cart cart) {
        if (!isApplicableBase(cart)) return false;
        
        int totalBuyQuantity = cart.getItems().stream()
                .filter(item -> buyProductIds.contains(item.getProductId()))
                .mapToInt(CartItem::getQuantity)
                .sum();
        
        return totalBuyQuantity >= buyQuantity;
    }
    
    @Override
    public DiscountResult calculateDiscount(Cart cart) {
        if (!isApplicable(cart)) {
            return new DiscountResult(0.0, "Coupon not applicable");
        }
        
        int totalBuyQuantity = cart.getItems().stream()
                .filter(item -> buyProductIds.contains(item.getProductId()))
                .mapToInt(CartItem::getQuantity)
                .sum();
        
        int applicableTimes = totalBuyQuantity / buyQuantity;
        if (repetitionLimit != null) {
            applicableTimes = Math.min(applicableTimes, repetitionLimit);
        }
        
        List<CartItem> getItems = cart.getItems().stream()
                .filter(item -> getProductIds.contains(item.getProductId()))
                .sorted(Comparator.comparingDouble(CartItem::getPrice))
                .collect(Collectors.toList());
        
        double totalDiscount = 0.0;
        int remainingFreeItems = applicableTimes * getQuantity;
        
        for (CartItem item : getItems) {
            if (remainingFreeItems <= 0) break;
            
            int freeQuantity = Math.min(item.getQuantity(), remainingFreeItems);
            totalDiscount += freeQuantity * item.getPrice();
            remainingFreeItems -= freeQuantity;
        }
        
        if (getMaxDiscountAmount() != null && totalDiscount > getMaxDiscountAmount()) {
            totalDiscount = getMaxDiscountAmount();
        }
        
        return new DiscountResult(totalDiscount, 
            String.format("BxGy discount applied %d times", applicableTimes));
    }
}