package com.microservice.service;

import com.microservice.model.Deposit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DepositService {

    Flux<Deposit> getAllDeposit();
    Mono<Deposit> getByIdDeposit(String id);
    Mono<Deposit> createDeposit(Deposit deposit);

}
