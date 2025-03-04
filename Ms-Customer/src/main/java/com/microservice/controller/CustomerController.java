package com.microservice.controller;

import com.microservice.model.Customer;
import com.microservice.model.CustomerSummary;
import com.microservice.service.CustomerService;
import com.microservice.service.client.CustomerSummaryClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/customer")
public class CustomerController {

    private final CustomerService service;
    private final CustomerSummaryClient client;

    private static final String CUSTOMER = "customer";

    @GetMapping(value = "/getAllCustomers")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Customer> getAllCustomers() {
        System.out.println("Listar todos los clientes");
        return service.getAllCustomer();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Customer> getByIdCustomer(@PathVariable String id) {
        System.out.println("Buscar cliente por su ID");
        return service.getByIdCustomer(id);
    }

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = CUSTOMER, fallbackMethod = "customer")
    public Mono<Customer> createCustomer(@RequestBody Customer customer) {
        System.out.println("Cliente creado con Éxito");
        return service.createCustomer(customer);
    }

    @PutMapping(value = "/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = CUSTOMER, fallbackMethod = "customer")
    public Mono<Customer> updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
        System.out.println("Cliente actualizado con Éxito");
        return service.updateCustomer(id, customer);
    }

    @DeleteMapping(value = "/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteCustomer(@PathVariable String id) {
        System.out.println("Cliente Eliminado con Éxito");
        return service.deleteCustomer(id);
    }

    @GetMapping("/customer/{id}/summary")
    public Mono<CustomerSummary> getCustomerSummary(@PathVariable String id) {
        return client.getCustomerSummary(id);
    }

}
