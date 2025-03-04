package com.microservice.controller;

import com.microservice.model.Transferer;
import com.microservice.service.impl.TransfererServiceImpl;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transferer")
public class TransfererController {

    private final TransfererServiceImpl transfererService;

    private static final String TRANSFERER = "transferer";

    @GetMapping(value = "/getAllTransferer")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Transferer> getAllTransferer(){
        System.out.println("Listar las transferencias.");
        return transfererService.getAllTransferer();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Transferer> getByIdTransferer(@PathVariable String id){
        System.out.println("Listar transferencia por ID.");
        return transfererService.getByIdTransferer(id);
    }

    @PostMapping(value = "/create")
    @CircuitBreaker(name = TRANSFERER, fallbackMethod = "transferer")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Transferer>> createTransferer(@RequestBody Transferer transferer){
        System.out.println("Transferencia creada con Éxito.");
        return transfererService.createTransferer(transferer)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping(value = "/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = TRANSFERER, fallbackMethod = "transferer")
    public Mono<Transferer> updateTransferer(@PathVariable String id, @RequestBody Transferer transferer){
        System.out.println("Transferencia actualizada con Éxito.");
        return transfererService.updateTransferer(id, transferer);
    }

    @DeleteMapping(value = "/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteTransferer(@PathVariable String id){
        System.out.println("Transferencia eliminada con Éxito.");
        return transfererService.deleteTransferer(id);
    }

}
