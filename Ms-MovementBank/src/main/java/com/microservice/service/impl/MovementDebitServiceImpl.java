package com.microservice.service.impl;

import com.microservice.exception.BusinessException;
import com.microservice.model.Account;
import com.microservice.model.MovementDebit;
import com.microservice.repository.MovementDebitRepository;
import com.microservice.service.MovementDebitService;
import com.microservice.service.client.AccountClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
public class MovementDebitServiceImpl implements MovementDebitService {

    @Autowired
    MovementDebitRepository movementDebitRepository;

    @Autowired
    AccountClient accountClient;

    @Override
    public Flux<MovementDebit> getAllMovementDebit() {
        return movementDebitRepository.findAll();
    }

    @Override
    public Mono<MovementDebit> getByIdMovementDebit(String id) {
        return movementDebitRepository.findById(id);
    }


    private static final Map<String, Integer> TRANSACTION_LIMITS = Map.of(
            "AHORRO", 5,
            "CORRIENTE", 10
    );

    private static final Map<String, Double> COMMISSIONS = Map.of(
            "AHORRO", 1.00,
            "CORRIENTE", 0.50
    );

    @Override
    public Mono<MovementDebit> createMovementDebit(MovementDebit movementDebit) {
        return accountClient.getAccountById(movementDebit.getIdAccount())
                .switchIfEmpty(Mono.error(new BusinessException("La cuenta bancaria no existe.")))
                .flatMap(account -> {
                    double commission;
                    int maxFreeTransactions = 5;
                    boolean isWithdrawal = movementDebit.getAmount() < 0;

                    if (isWithdrawal && account.getAvailableBalanceAccount() < Math.abs(movementDebit.getAmount())) {
                        return Mono.error(new BusinessException("Saldo insuficiente para realizar el retiro."));
                    }

                   if (account.getTransactionCount() >= maxFreeTransactions) {
                        commission = 2.5;
                    } else {
                       commission = 0.0;
                   }

                    double finalAmount = movementDebit.getAmount() - commission;
                    account.setAvailableBalanceAccount(account.getAvailableBalanceAccount() + finalAmount);

                    account.setTransactionCount(account.getTransactionCount() + 1);

                    return accountClient.updateAccount(account)
                            .then(Mono.defer(() -> {
                                movementDebit.setCommission(commission);
                                return movementDebitRepository.save(movementDebit);
                            }));
                });

    }

    @Override
    public Mono<MovementDebit> updateMovementDebit(String id, MovementDebit movementDebit) {
        return movementDebitRepository.findById(id).flatMap(movementDebit1 -> {
            movementDebit1.setAmount(movementDebit.getAmount());
            movementDebit1.setDateLimit(movementDebit.getDateLimit());
            movementDebit1.setCommission(movementDebit.getCommission());
            movementDebit1.setDescription(movementDebit.getDescription());
            return movementDebitRepository.save(movementDebit1);
        }).switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> deleteMovementDebit(String id) {
        return movementDebitRepository.findById(id).flatMap(movementDebit -> movementDebitRepository.deleteById(movementDebit.getId()));
    }
}
