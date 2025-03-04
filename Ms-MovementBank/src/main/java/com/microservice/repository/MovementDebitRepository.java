package com.microservice.repository;

import com.microservice.model.MovementDebit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MovementDebitRepository extends ReactiveMongoRepository<MovementDebit, String> {

}
