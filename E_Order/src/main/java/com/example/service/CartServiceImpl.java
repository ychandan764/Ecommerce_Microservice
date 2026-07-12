package com.example.service;

import com.example.clients.ProductServiceClient;
import com.example.clients.UserServiceClient;
import com.example.dto.request.CartItemRequest;
import com.example.dto.response.ProductResponse;
import com.example.dto.response.UserResponse;
import com.example.entity.CartItem;
import com.example.exception.ProductNotAvailableException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.CartItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {


    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;



    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackAddToCart")
    public Boolean addToCart(String userId, CartItemRequest request) {

        ProductResponse product;
        try {
            log.info("Fetching product details for ID: {}", request.getProductId());
            product = productServiceClient.getProductById(request.getProductId());
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Product not found in Product Service for ID: {}", request.getProductId());
            throw new ResourceNotFoundException("Product not found with id: " + request.getProductId());
        } catch (Exception e) {
            log.error("Error calling Product Service for ID: {}: {}", request.getProductId(), e.getMessage());
            throw e;
        }

        if (product == null) {
            log.error("Product Service returned null for ID: {}", request.getProductId());
            throw new ResourceNotFoundException("Product not found with id: " + request.getProductId());
        }

        if (product.getActive() == null || !product.getActive()) {
            throw new ProductNotAvailableException("Product is not active: " + product.getName());
        }

        if (product.getStockQuantity() == null || product.getStockQuantity() < request.getQuantity()) {
            throw new ProductNotAvailableException("Insufficient stock for product: " + product.getName());
        }

        UserResponse user = userServiceClient.getUserById(Long.valueOf(userId));
        if (user == null) {
            return false;
        }

        Optional<CartItem> existing =
                cartItemRepository.findByUserIdAndProductId(userId, request.getProductId());

        if (existing.isPresent()) {
            CartItem cartItem = existing.get();
            int updatedQty = cartItem.getQuantity() + request.getQuantity();
            
            if (product.getStockQuantity() < updatedQty) {
                throw new ProductNotAvailableException("Insufficient stock for total quantity: " + product.getName());
            }

            cartItem.setQuantity(updatedQty);
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(updatedQty)));
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setUnitPrice(product.getPrice());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItemRepository.save(cartItem);
        }


        return true;
    }

    @Override
    public boolean removeFromCart(String userId, Long productId) {
        Optional<CartItem> cartItem =
                cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (cartItem.isEmpty()) {
            return false;
        }

        cartItemRepository.delete(cartItem.get());
        return true;
    }

    @Override
    public List<CartItem> viewCart(String userId) {
        return cartItemRepository.findAllByUserId(userId);
    }

    public Boolean fallbackAddToCart(String userId, CartItemRequest request, Throwable throwable) {
        log.error("Fallback method called for addToCart due to: {}", throwable.getMessage());
        return false;
    }
}