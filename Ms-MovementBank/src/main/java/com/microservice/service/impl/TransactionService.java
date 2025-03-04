package com.microservice.service.impl;

import com.microservice.model.Transaction;
import com.microservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Flux<Transaction> getLast10Transactions(String cardId) {
        return transactionRepository.findByCardIdOrderByDateDesc(cardId, PageRequest.of(0, 10));
    }
}

