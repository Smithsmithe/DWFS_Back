package com.unir.books.orders.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @LoadBalanced
    @Bean("loadBalancedRestClient")
    public RestClient.Builder loadBalancedRestClient() {
        return RestClient.builder();
    }

    @Primary
    @Bean("plainRestClient")
    public RestClient.Builder plainRestClient() {
        return RestClient.builder();
    }
}