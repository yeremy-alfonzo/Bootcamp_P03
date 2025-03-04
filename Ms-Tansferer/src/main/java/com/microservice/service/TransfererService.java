package com.microservice.service;

import com.microservice.exception.BusinessException;
import com.microservice.model.Account;
import com.microservice.model.MovementCredit;
import com.microservice.model.MovementDebit;
import com.microservice.model.Transferer;
import com.microservice.repository.TransfererRepository;
import com.microservice.service.impl.TransfererServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransfererService implements TransfererServiceImpl {

    private final WebClient webClient;

    private final TransfererRepository transfererRepository;

    public TransfererService(WebClient.Builder webClientBuilder, TransfererRepository transfererRepository) {
        this.webClient = webClientBuilder.baseUrl(accountServiceUrl).build();
        this.transfererRepository = transfererRepository;
    }

    @Value("${http://localhost:8085}")
    private String accountServiceUrl;

    @Value("${http://localhost:8090}")
    private String movementServiceUrl;

    @Override
    public Flux<Transferer> getAllTransferer() {
        return transfererRepository.findAll();
    }

    @Override
    public Mono<Transferer> getByIdTransferer(String id) {
        return transfererRepository.findById(id);
    }

    @Override
    public Mono<Transferer> createTransferer(Transferer transferer) {
        if (transferer.getAmount() <= 0) {
            return Mono.error(new BusinessException("El monto de la transferencia debe ser mayor a cero."));
        }

        return webClient.get()
                .uri(accountServiceUrl + "/accounts/{id}", transferer.getIdAccountOrigin())
                .retrieve()
                .bodyToMono(Account.class)
                .switchIfEmpty(Mono.error(new BusinessException("La cuenta de origen no existe.")))
                .flatMap(originAccount -> {

                    return webClient.get()
                            .uri(accountServiceUrl + "/accounts/{id}", transferer.getIdAccountDestination())
                            .retrieve()
                            .bodyToMono(Account.class)
                            .switchIfEmpty(Mono.error(new BusinessException("La cuenta de destino no existe.")))
                            .flatMap(destinationAccount -> {
                                boolean isSameOwner = originAccount.getIdCustomer().equals(destinationAccount.getIdCustomer());
                                double commission = isSameOwner ? 0.0 : 2.5; // Comisión de 2.5 para terceros
                                double totalAmount = transferer.getAmount() + commission;

                                if (originAccount.getAvailableBalanceAccount() < totalAmount) {
                                    return Mono.error(new BusinessException("Saldo insuficiente considerando la comisión."));
                                }

                                originAccount.setAvailableBalanceAccount(originAccount.getAvailableBalanceAccount() - totalAmount);
                                destinationAccount.setAvailableBalanceAccount(destinationAccount.getAvailableBalanceAccount() + transferer.getAmount());

                                Mono<Void> updateOrigin = webClient.put()
                                        .uri(accountServiceUrl + "/accounts/{id}", originAccount.getId())
                                        .bodyValue(originAccount)
                                        .retrieve()
                                        .bodyToMono(Void.class);

                                Mono<Void> updateDestination = webClient.put()
                                        .uri(accountServiceUrl + "/accounts/{id}", destinationAccount.getId())
                                        .bodyValue(destinationAccount)
                                        .retrieve()
                                        .bodyToMono(Void.class);

                                Mono<Transferer> saveTransfer = transfererRepository.save(transferer);

                                Mono<Void> registerDebitMovement = webClient.post()
                                        .uri(movementServiceUrl + "/debits")
                                        .bodyValue(new MovementDebit(transferer.getIdAccountOrigin(), -transferer.getAmount(), commission, "Transferencia a cuenta " + destinationAccount.getId()))
                                        .retrieve()
                                        .bodyToMono(Void.class);

                                Mono<Void> registerCreditMovement = webClient.post()
                                        .uri(movementServiceUrl + "/credits")
                                        .bodyValue(new MovementCredit(transferer.getIdAccountDestination(), transferer.getAmount(), "Transferencia recibida de cuenta " + originAccount.getId()))
                                        .retrieve()
                                        .bodyToMono(Void.class);

                                return updateOrigin
                                        .then(updateDestination)
                                        .then(saveTransfer)
                                        .then(registerDebitMovement)
                                        .then(registerCreditMovement)
                                        .thenReturn(transferer);
                            });
                });
    }


    @Override
    public Mono<Transferer> updateTransferer(String id, Transferer transferer) {
        return transfererRepository.findById(id).flatMap(transferer1 -> {
            transferer1.setAmount(transferer.getAmount());
            transferer1.setTypeTransfer(transferer.getTypeTransfer());
            transferer1.setDescription(transferer.getDescription());
            return transfererRepository.save(transferer1);
        }).switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> deleteTransferer(String id) {
        return transfererRepository.findById(id).flatMap(transferer -> transfererRepository.deleteById(transferer.getId()));
    }
}
