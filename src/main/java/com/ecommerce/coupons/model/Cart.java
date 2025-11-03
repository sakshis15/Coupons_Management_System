package com.ecommerce.coupons.model;


import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class Cart {
    private String id;
    private List<CartItem> items = new ArrayList<>();
    
    public Double getTotalAmount() { 
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
    
    public void addItem(CartItem item) {
        this.items.add(item);
    }

	public void setTotalAmount(double d) {
		// TODO Auto-generated method stub
		
	}
}