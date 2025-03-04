package com.microservice.service.client;

import com.microservice.model.Customer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CustomerClient {
    private final WebClient webClient;

    public CustomerClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8090").build();
    }

    public Mono<Customer> getCustomerById(String id) {
        return webClient.get()
                .uri("/customer/{id}", id)
                .retrieve()
                .bodyToMono(Customer.class);
    }
}