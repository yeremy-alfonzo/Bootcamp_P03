package com.microservice.service.impl;

import com.microservice.exception.BusinessException;
import com.microservice.model.MovementCredit;
import com.microservice.repository.MovementCreditRepository;
import com.microservice.service.MovementCreditService;
import com.microservice.service.client.CreditClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovementCreditServiceImpl implements MovementCreditService {

    @Autowired
    MovementCreditRepository movementCreditRepository;

    @Autowired
    CreditClient creditClient;

    @Override
    public Flux<MovementCredit> getAllMovementCredits() {
        return movementCreditRepository.findAll();
    }

    @Override
    public Mono<MovementCredit> getMovementCreditById(String id) {
        return movementCreditRepository.findById(id);
    }

    @Override
    public Mono<MovementCredit> createMovementCredit(MovementCredit movementCredit) {
        return creditClient.getCreditById(movementCredit.getIdCredit())
                .switchIfEmpty(Mono.error(new BusinessException("El crédito no existe.")))
                .flatMap(credit -> {
                    if (movementCredit.getAmount() <= 0) {
                        return Mono.error(new BusinessException("El monto del pago debe ser mayor a 0."));
                    }

                    credit.setAmount(credit.getAmount() - movementCredit.getAmount());

                    return creditClient.updateCredit(credit)
                            .then(movementCreditRepository.save(movementCredit));
                });
    }

    @Override
    public Mono<MovementCredit> updateMovementCredit(String id, MovementCredit movementCredit) {
        return movementCreditRepository.findById(id).flatMap(movementCredit1 -> {
            movementCredit1.setAmount(movementCredit.getAmount());
            movementCredit1.setDateLimit(movementCredit.getDateLimit());
            movementCredit1.setCommission(movementCredit.getCommission());
            movementCredit1.setDescription(movementCredit.getDescription());
            return movementCreditRepository.save(movementCredit1);
        }).switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> deleteMovementCredit(String id) {
        return movementCreditRepository.findById(id).flatMap(movementCredit -> movementCreditRepository.deleteById(movementCredit.getId()));
    }
}
