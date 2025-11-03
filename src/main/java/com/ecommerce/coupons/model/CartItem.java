package com.ecommerce.coupons.model;


import lombok.Data;

@Data
public class CartItem {
    private String productId;
    private String productName;
    private Double price;
    private Integer quantity;
    
    public Double getTotalPrice() {
        return price * quantity;
    }
}
