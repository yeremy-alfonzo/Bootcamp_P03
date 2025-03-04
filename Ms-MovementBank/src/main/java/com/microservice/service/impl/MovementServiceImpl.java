package com.microservice.service.impl;

import com.microservice.exception.BusinessException;
import com.microservice.model.Movement;
import com.microservice.repository.MovementRepository;
import com.microservice.service.MovementService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovementServiceImpl implements MovementService {
    private final MovementRepository movementRepository;

    public MovementServiceImpl(MovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }

    @Override
    public Flux<Movement> getMovementsByProductId(String productId) {
        return movementRepository.findByIdProduct(productId)
                .switchIfEmpty(Mono.error(new BusinessException("No se encontraron movimientos para este producto.")));
    }
}
