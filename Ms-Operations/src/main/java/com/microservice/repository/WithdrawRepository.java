package com.microservice.repository;

import com.microservice.model.Withdraw;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface WithdrawRepository extends ReactiveMongoRepository<Withdraw, String> {
}
