package com.microservice.service;

import com.microservice.exception.BusinessException;
import com.microservice.model.Account;
import com.microservice.model.DebitCard;
import com.microservice.model.MovementDebit;
import com.microservice.model.PaymentRequest;
import com.microservice.repository.AccountRepository;
import com.microservice.repository.DebitCardRepository;
import com.microservice.repository.MovementDebitRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DebitCardService {
    private final WebClient webClient;
    private final AccountRepository accountRepository;
    private final MovementDebitRepository movementDebitRepository;
    private final DebitCardRepository debitCardRepository;

    public DebitCardService(WebClient.Builder webClientBuilder,
                            AccountRepository accountRepository,
                            MovementDebitRepository movementDebitRepository, DebitCardRepository debitCardRepository) {
        this.webClient = webClientBuilder.build();
        this.accountRepository = accountRepository;
        this.movementDebitRepository = movementDebitRepository;
        this.debitCardRepository = debitCardRepository;
    }

    public Mono<String> payWithDebitCard(PaymentRequest paymentRequest) {
        return accountRepository.findById(paymentRequest.getAccountId())
                .switchIfEmpty(Mono.error(new BusinessException("Cuenta no encontrada.")))
                .flatMap(account -> {
                    if (account.getAvailableBalanceAccount() < 0) {
                        return Mono.error(new BusinessException("Saldo insuficiente."));
                    }

                    account.setAvailableBalanceAccount(account.getAvailableBalanceAccount());

                    MovementDebit movement = new MovementDebit();
                    movement.setIdAccount(account.getId());
                    movement.setAmount(paymentRequest.getAmount().negate());
                    movement.setDescription("Pago con tarjeta de débito");
                    movement.setDate(LocalDateTime.now());

                    return accountRepository.save(account)
                            .then(movementDebitRepository.save(movement))
                            .then(Mono.just("Pago realizado con éxito."));
                });
    }

    public Mono<Void> associateAllAccountsToCard(String cardId) {
        return debitCardRepository.findById(cardId)
                .switchIfEmpty(Mono.error(new BusinessException("Tarjeta no encontrada.")))
                .flatMap(debitCard ->
                        accountRepository.findByIdCustomer(debitCard.getCustomerId())
                                .map(Account::getId)
                                .collectList()
                                .flatMap(accountIds -> {
                                    debitCard.setAccountIds(accountIds);
                                    return debitCardRepository.save(debitCard);
                                })
                ).then();
    }

    public Mono<Void> setPrimaryAccount(String cardId, String accountId) {
        return debitCardRepository.findById(cardId)
                .switchIfEmpty(Mono.error(new BusinessException("Tarjeta no encontrada.")))
                .flatMap(debitCard -> {
                    if (!debitCard.getAccountIds().contains(accountId)) {
                        return Mono.error(new BusinessException("La cuenta no está asociada a esta tarjeta."));
                    }
                    debitCard.setPrimaryAccountId(accountId);
                    return debitCardRepository.save(debitCard);
                })
                .then();
    }

    public Mono<Void> processDebitTransaction(String debitCardId, BigDecimal amount) {
        return debitCardRepository.findById(debitCardId)
                .flatMap(debitCard -> attemptTransaction(debitCard, amount));
    }

    private Mono<Void> attemptTransaction(DebitCard debitCard, BigDecimal amount) {
        List<String> accountIds = debitCard.getAccountIds();
        return processPayment(accountIds, amount);
    }

    private Mono<Void> processPayment(List<String> accountIds, BigDecimal amount) {
        if (accountIds.isEmpty()) {
            return Mono.error(new BusinessException("Saldo insuficiente en todas las cuentas asociadas."));
        }

        String currentAccountId = accountIds.get(0);

        return accountRepository.findById(currentAccountId)
                .flatMap(account -> {
                    BigDecimal availableBalance = account.getBalance();

                    if (availableBalance.compareTo(amount) >= 0) {
                        account.setBalance(availableBalance.subtract(amount));
                        return accountRepository.save(account).then();
                    } else {
                        BigDecimal remainingAmount = amount.subtract(availableBalance);
                        account.setBalance(BigDecimal.ZERO);

                        return accountRepository.save(account)
                                .then(processPayment(accountIds.subList(1, accountIds.size()), remainingAmount));
                    }
                });
    }

    public Mono<BigDecimal> getPrimaryAccountBalance(String cardNumber) {
        return debitCardRepository.findByCardNumber(cardNumber)
                .flatMap(debitCard -> accountRepository.findById(debitCard.getPrimaryAccountId()))
                .map(Account::getBalance)
                .switchIfEmpty(Mono.error(new BusinessException("Cuenta principal no encontrada.")));
    }
}
