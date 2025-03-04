package com.microservice.service.impl;

import com.microservice.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountServiceImpl {

    Flux<Account> getAllAccounts();

    Mono<Account> getByIdAccount(String id);

    Mono<Account> createAccount(Account account);

    Mono<Account> updateAccount(String id, Account account);

    Mono<Void> deleteAccount(String id);

    Mono<Double> getAccountBalance(String accountId);

}
