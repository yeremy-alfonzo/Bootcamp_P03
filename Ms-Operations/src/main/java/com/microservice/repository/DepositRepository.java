package com.microservice.repository;

import com.microservice.model.Deposit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DepositRepository extends ReactiveMongoRepository<Deposit, String> {
}
