package com.example.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @GetMapping("/message")
    @RateLimiter(name = "rateBreaker", fallbackMethod = "fallbackMessage")
    public String getMessage() {
        return "Hello from the API!";
    }

    public String fallbackMessage(Throwable t) {
        return "Message Fallback: " + t.getMessage();
    }

}