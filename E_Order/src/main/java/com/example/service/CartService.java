package com.example.service;

import com.example.dto.request.CartItemRequest;
import com.example.entity.CartItem;

import java.util.List;

public interface CartService {
	
	Boolean addToCart(String userId, CartItemRequest request);

	boolean removeFromCart(String userId, Long productId);

	List<CartItem> viewCart(String userId);
}