package com.microservice.service.impl;

import com.microservice.model.Deposit;
import com.microservice.repository.DepositRepository;
import com.microservice.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class DepositServiceImpl implements DepositService {

    @Autowired
    DepositRepository depositRepository;

    @Override
    public Flux<Deposit> getAllDeposit() {
        return depositRepository.findAll();
    }

    @Override
    public Mono<Deposit> getByIdDeposit(String id) {
        return depositRepository.findById(id);
    }

    @Override
    public Mono<Deposit> createDeposit(Deposit deposit) {
        deposit.setDateStart(new Date());
        return depositRepository.save(deposit);
    }
}
