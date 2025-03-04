package com.microservice.service;

import com.microservice.exception.BusinessException;
import com.microservice.model.Credit;
import com.microservice.model.Product;
import com.microservice.repository.CreditRepository;
import com.microservice.repository.ProductRepository;
import com.microservice.service.client.CustomerClient;
import com.microservice.service.impl.CreditServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreditService implements CreditServiceImpl {

    private final CreditRepository creditRepository;
    private final CustomerClient customerClient;

    @Override
    public Flux<Credit> getAllCredits() {
        return creditRepository.findAll();
    }

    @Override
    public Mono<Credit> getByIdCredit(String id) {
        return creditRepository.findById(id);
    }

    @Override
    public Mono<Credit> createCredit(Credit credit) {

        return customerClient.getCustomerById(credit.getCustomerId())
                .switchIfEmpty(Mono.error(new BusinessException("El cliente no existe.")))
                .flatMap(customer -> {
                    if (credit.getTypeCredit() == null || credit.getTypeCredit().isEmpty()) {
                        return Mono.error(new BusinessException("El tipo de crédito es obligatorio."));
                    }

                    if (credit.getIssuerBank() == null || credit.getIssuerBank().isEmpty()) {
                        return Mono.error(new BusinessException("El banco emisor es obligatorio."));
                    }

                    if ("TARJETA_CREDITO".equalsIgnoreCase(credit.getTypeCredit())) {
                        if (credit.getCardIssuer() == null || credit.getCardIssuer().isEmpty()) {
                            return Mono.error(new BusinessException("El emisor de la tarjeta es obligatorio."));
                        }
                        if (credit.getCodeCVV() == null) {
                            return Mono.error(new BusinessException("El código CVV es obligatorio."));
                        }
                    }

                    return creditRepository.save(credit);
                });
    }

    @Override
    public Mono<Credit> updateCredit(Credit credit) {
        return creditRepository.findById(credit.getId())
                .switchIfEmpty(Mono.error(new BusinessException("El crédito no existe.")))
                .flatMap(existingCredit -> {
                    existingCredit.setAmount(credit.getAmount());
                    return creditRepository.save(existingCredit);
                });
    }

    @Override
    public Mono<Credit> registerConsumption(String creditId, double amount) {
        return creditRepository.findById(creditId)
                .switchIfEmpty(Mono.error(new BusinessException("La tarjeta de crédito no existe.")))
                .flatMap(credit -> {
                    if (!"TARJETA_CREDITO".equalsIgnoreCase(credit.getTypeCredit())) {
                        return Mono.error(new BusinessException("Este producto no es una tarjeta de crédito."));
                    }

                    double newDebt = credit.getCurrentDebt() + amount;
                    if (newDebt > credit.getCreditLimit()) {
                        return Mono.error(new BusinessException("El consumo excede el límite de crédito."));
                    }

                    credit.setCurrentDebt(newDebt);
                    return creditRepository.save(credit);
                });
    }

    @Override
    public Mono<Double> getAvailableCredit(String creditId) {
        return creditRepository.findById(creditId)
                .map(credit -> credit.getCreditLimit() - credit.getCurrentDebt())
                .switchIfEmpty(Mono.error(new BusinessException("Tarjeta de crédito no encontrada")));
    }




}
