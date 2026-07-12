package com.example.service;

import com.example.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(String userId);
}
