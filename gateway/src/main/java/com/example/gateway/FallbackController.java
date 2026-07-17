package com.example.gateway;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Hidden
public class FallbackController {

    @GetMapping("/fallback/product")
    public ResponseEntity<List<String>> productServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(List.of("Product Service is currently unavailable. Please try again later."));
    }

}
