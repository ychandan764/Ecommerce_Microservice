package com.example.clients;

import com.example.dto.response.ProductResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api/v1/products")
public interface ProductServiceClient {

    @GetExchange("/{id}")
    ProductResponse getProductById(@PathVariable(name = "id") Long id);
}