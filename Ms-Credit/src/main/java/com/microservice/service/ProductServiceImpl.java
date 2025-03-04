package com.microservice.service;

import com.microservice.model.Product;
import com.microservice.repository.ProductRepository;
import com.microservice.service.client.CustomerClient;
import com.microservice.service.impl.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CustomerClient customerClient;


    @Override
    public Mono<Product> assignProductToCustomer(String customerId) {
        return null;
    }
}
