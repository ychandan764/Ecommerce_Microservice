package com.example.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class UserServiceClientConfig {

    @Value("${clients.user-service.url:http://localhost:8081}")
    private String userServiceClientBaseUrl;

    @Bean
    public UserServiceClient userServiceClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(userServiceClientBaseUrl)
                .build();
        
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        
        return httpServiceProxyFactory.createClient(UserServiceClient.class);
    }
}
