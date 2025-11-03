package com.ecommerce.coupons.service;


import com.ecommerce.coupons.model.*;
import com.ecommerce.coupons.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {
    
    private final CouponRepository couponRepository;
    
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }
    
    public Optional<Coupon> getCouponById(Long id) {
        return couponRepository.findById(id);
    }
    
    public Optional<Coupon> getCouponByCode(String code) {
        return couponRepository.findByCode(code);
    }
    
    public Coupon createCoupon(Coupon coupon) {
        log.info("Creating new coupon with code: {}", coupon.getCode());
        
        // Set default values if null
        if (coupon.getCurrentUsage() == null) {
            coupon.setCurrentUsage(0);
        }
        if (coupon.getStartDate() == null) {
            coupon.setStartDate(LocalDateTime.now());
        }
        
        return couponRepository.save(coupon);
    }
    
    public Coupon updateCoupon(Long id, Coupon couponDetails) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + id));
        
        coupon.setCode(couponDetails.getCode());
        coupon.setDescription(couponDetails.getDescription());
        coupon.setStartDate(couponDetails.getStartDate());
        coupon.setEndDate(couponDetails.getEndDate());
        coupon.setActive(couponDetails.isActive());
        coupon.setMaxUsage(couponDetails.getMaxUsage());
        coupon.setMinCartValue(couponDetails.getMinCartValue());
        coupon.setMaxDiscountAmount(couponDetails.getMaxDiscountAmount());
        
        log.info("Updating coupon with id: {}", id);
        return couponRepository.save(coupon);
    }
    
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new RuntimeException("Coupon not found with id: " + id);
        }
        log.info("Deleting coupon with id: {}", id);
        couponRepository.deleteById(id);
    }
    
    public List<ApplicableCoupon> getApplicableCoupons(Cart cart) {
        List<Coupon> activeCoupons = couponRepository.findActiveCoupons(LocalDateTime.now());
        
        log.info("Found {} active coupons, checking applicability for cart: {}", 
                 activeCoupons.size(), cart.getId());
        
        return activeCoupons.stream()
                .filter(coupon -> {
                    boolean applicable = coupon.isApplicable(cart);
                    if (applicable) {
                        log.debug("Coupon {} is applicable for cart {}", coupon.getCode(), cart.getId());
                    }
                    return applicable;
                })
                .map(coupon -> {
                    DiscountResult discount = coupon.calculateDiscount(cart);
                    return new ApplicableCoupon(coupon, discount);
                })
                .collect(Collectors.toList());
    }
    
    public Cart applyCouponToCart(Long couponId, Cart cart) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + couponId));
        
        if (!coupon.isApplicable(cart)) {
            throw new RuntimeException("Coupon " + coupon.getCode() + " is not applicable to this cart");
        }
        
        DiscountResult discount = coupon.calculateDiscount(cart);
        
        // In a real scenario, you'd update the cart in database
        // For now, we'll just return the cart with discount information
        cart.setTotalAmount(cart.getTotalAmount() - discount.getDiscountAmount());
        
        // Update coupon usage
        coupon.setCurrentUsage(coupon.getCurrentUsage() + 1);
        couponRepository.save(coupon);
        
        log.info("Applied coupon {} to cart {}, discount: {}", 
                 coupon.getCode(), cart.getId(), discount.getDiscountAmount());
        
        return cart;
    }
    
    public Cart applyCouponByCode(String couponCode, Cart cart) {
        Coupon coupon = couponRepository.findActiveCouponByCode(couponCode, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Active coupon not found with code: " + couponCode));
        
        return applyCouponToCart(coupon.getId(), cart);
    }
}