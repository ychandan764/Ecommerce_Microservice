package com.example.service;

import com.example.dto.request.ProductRequest;
import com.example.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long id);

    ProductResponse getActiveProductById(Long id);

    List<ProductResponse> getAllProducts();

    List<ProductResponse> getAllActiveProducts();

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
}