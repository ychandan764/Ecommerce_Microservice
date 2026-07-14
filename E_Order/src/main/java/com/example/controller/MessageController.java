package com.example.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "System", description = "Operational and resilience endpoints")
@RestController
public class MessageController {

    @Operation(
            summary = "Get a sample message",
            description = "Returns a simple message protected by the rate limiter.",
            responses = @ApiResponse(responseCode = "200", description = "Message returned successfully"))
    @GetMapping("/message")
    @RateLimiter(name = "rateBreaker", fallbackMethod = "fallbackMessage")
    public String getMessage() {
        return "Hello from the API!";
    }

    public String fallbackMessage(Throwable t) {
        return "Message Fallback: " + t.getMessage();
    }

}
