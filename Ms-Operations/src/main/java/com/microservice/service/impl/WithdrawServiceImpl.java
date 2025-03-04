package com.microservice.service.impl;

import com.microservice.model.Withdraw;
import com.microservice.repository.WithdrawRepository;
import com.microservice.service.WithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class WithdrawServiceImpl implements WithdrawService {

    @Autowired
    WithdrawRepository withdrawRepository;

    @Override
    public Flux<Withdraw> getAllWithdraw() {
        return withdrawRepository.findAll();
    }

    @Override
    public Mono<Withdraw> getByIdWithdraw(String id) {
        return withdrawRepository.findById(id);
    }

    @Override
    public Mono<Withdraw> createWithdraw(Withdraw withdraw) {
        withdraw.setDateStart(new Date());
        return withdrawRepository.save(withdraw);
    }
}
