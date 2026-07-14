package com.example.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter(){
        return new RedisRateLimiter(1, 2,1);
    }

    @Bean
    public KeyResolver hostNameKeyResolver(){
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/api/users/**")
                        .uri("lb://user-service"))
                .route("user-service-openapi", r -> r.path("/docs/user-service")
                        .filters(f -> f.setPath("/api-docs"))
                        .uri("lb://user-service"))
                .route("product-service", r -> r.path("/api/v1/products", "/api/v1/products/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("ecomBreaker")
                                        .setFallbackUri("forward:/fallback/product"))
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(hostNameKeyResolver())))
                        .uri("lb://product-service"))
                .route("product-service-openapi", r -> r.path("/docs/product-service")
                        .filters(f -> f.setPath("/api-docs"))
                        .uri("lb://product-service"))
                .route("order-service", r -> r.path("/api/orders", "/api/orders/**", "/api/cart", "/api/cart/**")
                        .uri("lb://order-service"))
                .route("order-service-openapi", r -> r.path("/docs/order-service")
                        .filters(f -> f.setPath("/api-docs"))
                        .uri("lb://order-service"))
                .route("eureka-server", r -> r.path("/eureka/web")
                        .filters(f -> f.setPath("/"))
                        .uri("http://localhost:8761"))
                .route("eureka-server-static", r -> r.path("/eureka/**")
                        .uri("http://localhost:8761"))
                .build();
    }
}
