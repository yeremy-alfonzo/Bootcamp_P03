package com.microservice.service.impl;

import com.microservice.model.Product;
import reactor.core.publisher.Mono;

public interface ProductService {
    Mono<Product> assignProductToCustomer(String customerId);
}
