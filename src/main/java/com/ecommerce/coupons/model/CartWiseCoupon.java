package com.ecommerce.coupons.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("CART_WISE")
public class CartWiseCoupon extends Coupon {
    private Double discountPercentage;
    private Double minCartAmount;
    
    @Override
    public boolean isApplicable(Cart cart) {
        if (!isApplicableBase(cart)) return false;
        if (minCartAmount != null && cart.getTotalAmount() < minCartAmount) return false;
        return true;
    }
    
    @Override
    public DiscountResult calculateDiscount(Cart cart) {
        if (!isApplicable(cart)) {
            return new DiscountResult(0.0, "Coupon not applicable");
        }
        
        double discountAmount = cart.getTotalAmount() * (discountPercentage / 100);
        
        if (getMaxDiscountAmount() != null && discountAmount > getMaxDiscountAmount()) {
            discountAmount = getMaxDiscountAmount();
        }
        
        return new DiscountResult(discountAmount, 
            String.format("Cart wise discount %.1f%% applied", discountPercentage));
    }
}