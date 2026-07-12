package com.example.controller;

import com.example.dto.request.CartItemRequest;
import com.example.entity.CartItem;
import com.example.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;
	
	@PostMapping
	public ResponseEntity<String> addToCart(@RequestHeader("X-User-ID") String userId 
			                              , @RequestBody CartItemRequest request){
		if(!cartService.addToCart(userId,request)) {
			return ResponseEntity.badRequest().body("Product not found");
		}
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/items/{productId}")
	public ResponseEntity<String> removeFromCart(@RequestHeader("X-User-ID") String userId, @PathVariable Long productId) {
		if(!cartService.removeFromCart(userId, productId)) {
			return ResponseEntity.badRequest().body("Product not found in cart");
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping
	public ResponseEntity<List<CartItem>> viewCart(@RequestHeader("X-User-ID") String userId) {
		return ResponseEntity.ok(cartService.viewCart(userId));
	}
}
