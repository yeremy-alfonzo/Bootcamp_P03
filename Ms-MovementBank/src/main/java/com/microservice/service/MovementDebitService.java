package com.microservice.service;

import com.microservice.model.MovementDebit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementDebitService {

    Flux<MovementDebit> getAllMovementDebit();
    Mono<MovementDebit> getByIdMovementDebit(String id);
    Mono<MovementDebit> createMovementDebit(MovementDebit movementDebit);
    Mono<MovementDebit> updateMovementDebit(String id, MovementDebit movementDebit);
    Mono<Void> deleteMovementDebit(String id);

}
