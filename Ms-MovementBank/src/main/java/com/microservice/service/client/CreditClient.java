package com.microservice.service.client;

import com.microservice.exception.BusinessException;
import com.microservice.model.Credit;
import com.microservice.model.PaymentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
public class CreditClient {

    private final WebClient webClient;

    public CreditClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8085").build();
    }

    public Mono<Credit> getCreditById(String id) {
        return webClient.get()
                .uri("/credit/{id}", id)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        Mono.error(new BusinessException("El crédito no existe."))
                )
                .bodyToMono(Credit.class);
    }

    public Mono<Void> updateCredit(Credit credit) {
        return webClient.put()
                .uri("/credit/update/{id}", credit.getId())
                .bodyValue(credit)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        Mono.error(new BusinessException("Error al actualizar el crédito."))
                )
                .bodyToMono(Void.class);
    }

    public Mono<String> payCredit(PaymentRequest paymentRequest) {
        return webClient.get()
                .uri( "/credits/{id}", paymentRequest.getCreditId())
                .retrieve()
                .bodyToMono(Credit.class)
                .flatMap(credit -> processPayment(credit, paymentRequest.getAmount()))
                .thenReturn("Pago realizado con éxito.")
                .onErrorMap(e -> new BusinessException(e.getMessage()));
    }

    private Mono<Credit> processPayment(Credit credit, BigDecimal amount) {
        if (credit.getOutstandingBalance().compareTo(amount) < 0) {
            return Mono.error(new BusinessException("El monto excede la deuda pendiente."));
        }

        credit.setOutstandingBalance(credit.getOutstandingBalance().subtract(amount));

        return webClient.put()
                .uri( "/credits/{id}", credit.getId())
                .bodyValue(credit)
                .retrieve()
                .bodyToMono(Credit.class);
    }
}
