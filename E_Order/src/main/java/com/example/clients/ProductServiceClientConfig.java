package com.example.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.OptionalLong;

@Configuration
public class ProductServiceClientConfig {

    @Value("${clients.product-service.url:http://localhost:8082}")
    private String productServiceClientBaseUrl;

    @Bean
    public ProductServiceClient productServiceClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(productServiceClientBaseUrl)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError,
                        (((request, response) -> OptionalLong.empty())))
                .build();
        

        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return httpServiceProxyFactory.createClient(ProductServiceClient.class);
    }
}
