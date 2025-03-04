package com.microservice.service;

import com.microservice.model.Withdraw;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WithdrawService {

    Flux<Withdraw> getAllWithdraw();
    Mono<Withdraw> getByIdWithdraw(String id);
    Mono<Withdraw> createWithdraw(Withdraw withdraw);

}
