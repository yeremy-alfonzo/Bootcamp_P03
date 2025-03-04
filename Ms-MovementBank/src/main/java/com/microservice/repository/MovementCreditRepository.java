package com.microservice.repository;

import com.microservice.model.MovementCredit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MovementCreditRepository extends ReactiveMongoRepository<MovementCredit, String> {

}
