package com.microservice.controller;

import com.microservice.exception.BusinessException;
import com.microservice.model.Credit;
import com.microservice.service.CreditService;
import com.microservice.service.ProductServiceImpl;
import com.microservice.service.client.CustomerClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/credit")
public class CreditController {

    private final CreditService creditService;
    private final CustomerClient customerClient;

    private static final String CREDIT = "credit";
    private final ProductServiceImpl productServiceImpl;

    @GetMapping(value = "/allCredits")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Credit> getAllCredits(){
        System.out.println("Listar todas las tarjetas de credito.");
        return creditService.getAllCredits();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Credit> getCreditById(@PathVariable String id){
        System.out.println("Listar tarjeta de credito por ID.");
        return creditService.getByIdCredit(id);
    }

    @PostMapping(value = "/create")
    @CircuitBreaker(name = CREDIT, fallbackMethod = "credit")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Credit> createCredit(@RequestBody Credit credit){
        System.out.println("Tarjeta de credito creada con Éxito");
        return creditService.createCredit(credit);
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<Credit>> updateCredit(@PathVariable String id, @RequestBody Credit credit) {
        credit.setId(id);
        return creditService.updateCredit(credit)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/consumption")
    public Mono<ResponseEntity<Credit>> registerConsumption(@PathVariable String id, @RequestBody Map<String, Double> request) {
        double amount = request.get("amount");
        return creditService.registerConsumption(id, amount)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{id}/availablecredit")
    public Mono<ResponseEntity<Double>> getAvailableCredit(@PathVariable String id) {
        return creditService.getAvailableCredit(id)
                .map(availableCredit -> ResponseEntity.ok(availableCredit))
                .onErrorResume(BusinessException.class, e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/credits/{customerId}/overdue")
    public Mono<Boolean> hasOverdueDebts(@PathVariable String customerId) {
        return productServiceImpl.assignProductToCustomer(customerId).hasElement();
    }
}
