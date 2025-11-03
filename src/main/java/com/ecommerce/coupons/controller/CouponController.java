package com.ecommerce.coupons.controller;


import com.ecommerce.coupons.model.Cart;
import com.ecommerce.coupons.model.Coupon;
import com.ecommerce.coupons.service.CouponService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {
    
    private final CouponService couponService;
    
    @PostMapping
    public ResponseEntity<?> createCoupon(@Valid @RequestBody Coupon coupon) {
        try {
            // Validate coupon type
            if (coupon.getCouponType() == null) {
                return ResponseEntity.badRequest().body("couponType is required");
            }
            
            Coupon createdCoupon = couponService.createCoupon(coupon);
            return ResponseEntity.ok(createdCoupon);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating coupon: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable("id") Long id) {
        return couponService.getCouponById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<Coupon> getCouponByCode(@PathVariable("code") String code) {
        return couponService.getCouponByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable ("id") Long id, @Valid @RequestBody Coupon couponDetails) {
        try {
            Coupon updatedCoupon = couponService.updateCoupon(id, couponDetails);
            return ResponseEntity.ok(updatedCoupon);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable ("id") Long id) {
        try {
            couponService.deleteCoupon(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/applicable")
    public ResponseEntity<List<?>> getApplicableCoupons(@RequestBody Cart cart) {
        List<?> applicableCoupons = couponService.getApplicableCoupons(cart);
        return ResponseEntity.ok(applicableCoupons);
    }
    
    @PostMapping("/apply/{id}")
    public ResponseEntity<Cart> applyCoupon(@PathVariable ("id") Long id, @RequestBody Cart cart) {
        try {
            Cart updatedCart = couponService.applyCouponToCart(id, cart);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/apply-by-code/{code}")
    public ResponseEntity<Cart> applyCouponByCode(@PathVariable ("code") String code, @RequestBody Cart cart) {
        try {
            Cart updatedCart = couponService.applyCouponByCode(code, cart);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
