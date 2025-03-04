package com.microservice.repository;

import com.microservice.model.MovementDebit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MovementDebitRepository extends ReactiveMongoRepository<MovementDebit, String> {
    Flux<MovementDebit> findByIdAccount(String accountId);
}

