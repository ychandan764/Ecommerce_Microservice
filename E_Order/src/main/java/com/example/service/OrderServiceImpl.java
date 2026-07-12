package com.example.service;

import com.example.clients.UserServiceClient;
import com.example.dto.response.OrderItemResponse;
import com.example.dto.response.OrderResponse;
import com.example.dto.response.UserResponse;
import com.example.entity.CartItem;
import com.example.entity.Order;
import com.example.entity.OrderItem;
import com.example.enums.OrderStatus;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.CartItemRepository;
import com.example.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserServiceClient userServiceClient;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routeKey;

    @Override
    @Transactional
    public OrderResponse createOrder(String userId) {


        try {
            log.info("Validating user with ID: {}", userId);
            UserResponse user = userServiceClient.getUserById(Long.valueOf(userId));
            if (user == null) {
                throw new ResourceNotFoundException("User not found with id: " + userId);
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.error("User not found in User Service for ID: {}", userId);
            throw new ResourceNotFoundException("User not found with id: " + userId);
        } catch (NumberFormatException e) {
            log.error("Invalid user ID format: {}", userId);
            throw new IllegalArgumentException("Invalid user ID format: " + userId);
        }

        List<CartItem> cartItems = cartItemRepository.findAllByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Cannot create order.");
        }

        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.CONFIRMED);

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(cartItem.getProductId());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getPrice());
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);


        rabbitTemplate.convertAndSend(exchangeName, routeKey, Map.of("OrderId",savedOrder.getId(),
                                                                       "Status","Created"));

        return mapToOrderResponse(savedOrder);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().toString())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .build();
    }
}