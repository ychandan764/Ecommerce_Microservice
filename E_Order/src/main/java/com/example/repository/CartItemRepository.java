package com.example.repository;

import com.example.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {


    Optional<CartItem> findByUserIdAndProductId(String userId, Long productId);

    List<CartItem> findAllByUserId(String userId);

    void deleteAllByUserId(String userId);
}
