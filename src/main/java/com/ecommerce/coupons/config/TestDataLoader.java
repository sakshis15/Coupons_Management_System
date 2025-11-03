package com.ecommerce.coupons.config;


import com.ecommerce.coupons.model.*;
import com.ecommerce.coupons.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class TestDataLoader implements CommandLineRunner {
    
    private final CouponRepository couponRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Cart-wise coupon
        CartWiseCoupon cartCoupon = new CartWiseCoupon();
        cartCoupon.setCode("CART10");
        cartCoupon.setDescription("10% off on carts over ₹100");
        cartCoupon.setDiscountPercentage(10.0);
        cartCoupon.setMinCartAmount(100.0);
        cartCoupon.setStartDate(LocalDateTime.now().minusDays(1));
        cartCoupon.setEndDate(LocalDateTime.now().plusDays(30));
        cartCoupon.setMaxUsage(100);
        
        // Product-wise coupon
        ProductWiseCoupon productCoupon = new ProductWiseCoupon();
        productCoupon.setCode("PROD20");
        productCoupon.setDescription("20% off on Product A and B");
        productCoupon.setApplicableProductIds(Arrays.asList("P1", "P2"));
        productCoupon.setDiscountPercentage(20.0);
        productCoupon.setStartDate(LocalDateTime.now().minusDays(1));
        productCoupon.setEndDate(LocalDateTime.now().plusDays(30));
        
        // BxGy coupon
        BxGyCoupon bxgyCoupon = new BxGyCoupon();
        bxgyCoupon.setCode("B2G1");
        bxgyCoupon.setDescription("Buy 2 from X,Y,Z get 1 from A,B free");
        bxgyCoupon.setBuyProductIds(Arrays.asList("P1", "P2", "P3"));
        bxgyCoupon.setGetProductIds(Arrays.asList("P4", "P5"));
        bxgyCoupon.setBuyQuantity(2);
        bxgyCoupon.setGetQuantity(1);
        bxgyCoupon.setRepetitionLimit(3);
        bxgyCoupon.setStartDate(LocalDateTime.now().minusDays(1));
        bxgyCoupon.setEndDate(LocalDateTime.now().plusDays(30));
        
        couponRepository.saveAll(Arrays.asList(cartCoupon, productCoupon, bxgyCoupon));
    }
}
