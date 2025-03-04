package com.microservice.service;

import com.microservice.model.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {

    Flux<Customer> getAllCustomer();
    Mono<Customer> getByIdCustomer(String id);
    Mono<Customer> createCustomer(Customer customer);
    Mono<Customer> updateCustomer(String id, Customer customer);
    Mono<Void> deleteCustomer(String id);

}
