package com.ecommerce.coupons.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

import jakarta.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("PRODUCT_WISE")
public class ProductWiseCoupon extends Coupon {
    @ElementCollection
    private List<String> applicableProductIds;
    
    private Double discountPercentage;
    private Double fixedDiscount;
    
    @Override
    public boolean isApplicable(Cart cart) {
        if (!isApplicableBase(cart)) return false;
        
        return cart.getItems().stream()
                .anyMatch(item -> applicableProductIds.contains(item.getProductId()));
    }
    
    @Override
    public DiscountResult calculateDiscount(Cart cart) {
        if (!isApplicable(cart)) {
            return new DiscountResult(0.0, "Coupon not applicable");
        }
        
        double totalDiscount = 0.0;
        
        for (CartItem item : cart.getItems()) {
            if (applicableProductIds.contains(item.getProductId())) {
                double itemDiscount = 0.0;
                
                if (discountPercentage != null) {
                    itemDiscount = item.getPrice() * item.getQuantity() * (discountPercentage / 100);
                } else if (fixedDiscount != null) {
                    itemDiscount = fixedDiscount * item.getQuantity();
                }
                
                itemDiscount = Math.min(itemDiscount, item.getPrice() * item.getQuantity());
                totalDiscount += itemDiscount;
            }
        }
        
        if (getMaxDiscountAmount() != null && totalDiscount > getMaxDiscountAmount()) {
            totalDiscount = getMaxDiscountAmount();
        }
        
        return new DiscountResult(totalDiscount, "Product wise discount applied");
    }
}