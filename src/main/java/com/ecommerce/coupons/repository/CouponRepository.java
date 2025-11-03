package com.ecommerce.coupons.repository;



import com.ecommerce.coupons.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    List<Coupon> findByActiveTrue();
    
    Optional<Coupon> findByCode(String code);
    
    @Query("SELECT c FROM Coupon c WHERE c.active = true AND " +
           "(c.startDate IS NULL OR c.startDate <= :now) AND " +
           "(c.endDate IS NULL OR c.endDate >= :now) AND " +
           "(c.maxUsage IS NULL OR c.currentUsage < c.maxUsage)")
    List<Coupon> findActiveCoupons(@Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Coupon c WHERE c.active = true AND c.code = :code AND " +
           "(c.startDate IS NULL OR c.startDate <= :now) AND " +
           "(c.endDate IS NULL OR c.endDate >= :now) AND " +
           "(c.maxUsage IS NULL OR c.currentUsage < c.maxUsage)")
    Optional<Coupon> findActiveCouponByCode(@Param("code") String code, @Param("now") LocalDateTime now);
}
