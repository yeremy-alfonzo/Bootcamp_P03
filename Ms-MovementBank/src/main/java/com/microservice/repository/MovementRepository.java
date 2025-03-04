package com.microservice.repository;

import com.microservice.model.Movement;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MovementRepository extends ReactiveMongoRepository<Movement, String> {
    Flux<Movement> findByIdProduct(String idProduct);
}
