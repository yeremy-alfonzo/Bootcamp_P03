package com.microservice.service;

import com.microservice.model.Movement;
import reactor.core.publisher.Flux;

public interface MovementService {
    Flux<Movement> getMovementsByProductId(String productId);
}
