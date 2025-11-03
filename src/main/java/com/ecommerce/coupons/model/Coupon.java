package com.ecommerce.coupons.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "coupon_type")
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "couponType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CartWiseCoupon.class, name = "CART_WISE"),
    @JsonSubTypes.Type(value = ProductWiseCoupon.class, name = "PRODUCT_WISE"),
    @JsonSubTypes.Type(value = BxGyCoupon.class, name = "BXGY")
})
public abstract class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String code;
    
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active = true;
    
    // Common coupon constraints
    private Integer maxUsage;
    private Integer currentUsage = 0;
    private Double minCartValue;
    private Double maxDiscountAmount;
    
    // Add this field for JSON serialization
    @Transient
    private String couponType;
    
    public abstract boolean isApplicable(Cart cart);
    public abstract DiscountResult calculateDiscount(Cart cart);
    
    protected boolean isApplicableBase(Cart cart) {
        if (!active) return false;
        if (startDate != null && LocalDateTime.now().isBefore(startDate)) return false;
        if (endDate != null && LocalDateTime.now().isAfter(endDate)) return false;
        if (maxUsage != null && currentUsage >= maxUsage) return false;
        if (minCartValue != null && cart.getTotalAmount() < minCartValue) return false;
        return true;
    }
    
    // Helper method to get discriminator value
    public String getCouponType() {
        if (this instanceof CartWiseCoupon) return "CART_WISE";
        if (this instanceof ProductWiseCoupon) return "PRODUCT_WISE";
        if (this instanceof BxGyCoupon) return "BXGY";
        return null;
    }
}