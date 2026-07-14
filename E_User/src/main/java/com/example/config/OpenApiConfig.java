package com.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${openapi.title:User Service API}")
    private String title;

    @Value("${openapi.description:REST API for user and address management.}")
    private String description;

    @Value("${openapi.version:1.0.0}")
    private String version;

    @Value("${openapi.server-url:/}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title(title)
                .description(description)
                .version(version))
                .servers(List.of(new Server().url(serverUrl)));
    }
}
