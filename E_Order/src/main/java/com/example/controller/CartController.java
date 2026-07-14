package com.example.controller;

import com.example.dto.request.CartItemRequest;
import com.example.entity.CartItem;
import com.example.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cart", description = "Shopping cart APIs")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;
	
    @Operation(
            summary = "Add an item to cart",
            description = "Adds a product to the cart of the user identified by the X-User-ID header.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Item added to cart"),
                    @ApiResponse(responseCode = "400", description = "Product could not be added",
                            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
            })
	@PostMapping
	public ResponseEntity<String> addToCart(@Parameter(description = "Authenticated user identifier") @RequestHeader("X-User-ID") String userId
			                              , @RequestBody CartItemRequest request){
		if(!cartService.addToCart(userId,request)) {
			return ResponseEntity.badRequest().body("Product not found");
		}
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

    @Operation(
            summary = "Remove an item from cart",
            description = "Removes a product from the user's cart.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item removed from cart"),
                    @ApiResponse(responseCode = "400", description = "Product was not present in cart",
                            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
            })
	@DeleteMapping("/items/{productId}")
	public ResponseEntity<String> removeFromCart(@Parameter(description = "Authenticated user identifier") @RequestHeader("X-User-ID") String userId, @Parameter(description = "Product identifier") @PathVariable Long productId) {
		if(!cartService.removeFromCart(userId, productId)) {
			return ResponseEntity.badRequest().body("Product not found in cart");
		}
		return ResponseEntity.ok().build();
	}

    @Operation(
            summary = "View cart",
            description = "Returns all items in the user's cart.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartItem.class)))
            })
	@GetMapping
	public ResponseEntity<List<CartItem>> viewCart(@Parameter(description = "Authenticated user identifier") @RequestHeader("X-User-ID") String userId) {
		return ResponseEntity.ok(cartService.viewCart(userId));
	}
}
