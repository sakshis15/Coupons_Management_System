package com.ecommerce.coupons.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicableCoupon {
    private Coupon coupon;
    private DiscountResult discountResult;
}