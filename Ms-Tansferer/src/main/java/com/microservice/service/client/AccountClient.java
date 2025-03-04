package com.microservice.service.client;

import com.microservice.exception.BusinessException;
import com.microservice.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AccountClient {
    private final WebClient webClient;

    public AccountClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public Mono<Account> getAccountById(String id) {
        return webClient.get()
                .uri("/account/{id}", id)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        Mono.error(new BusinessException("La cuenta bancaria no existe."))
                )
                .bodyToMono(Account.class);
    }

    public Mono<Void> updateAccount(Account account) {
        return webClient.put()
                .uri("/update/{id}", account.getId())
                .bodyValue(account)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        Mono.error(new BusinessException("Error al actualizar la cuenta bancaria."))
                )
                .bodyToMono(Void.class);
    }
}