package com.microservice.service.impl;

import com.microservice.model.Account;
import com.microservice.model.BalanceSummary;
import com.microservice.model.Credit;
import com.microservice.model.DailyBalance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ReportService {

    private final WebClient webClient;

    @Value("${localhost:8080}")
    private String accountServiceUrl;

    @Value("${localhost:8085}")
    private String creditServiceUrl;

    @Value("${localhost:8090}")
    private String movementServiceUrl;

    public ReportService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<List<BalanceSummary>> generateBalanceSummary(String customerId) {
        return getAccountsByCustomer(customerId)
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::calculateAverageBalanceForAccount)
                .mergeWith(getCreditsByCustomer(customerId)
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(this::calculateAverageBalanceForCredit))
                .collectList();
    }

    private Mono<List<Account>> getAccountsByCustomer(String customerId) {
        return webClient.get()
                .uri(accountServiceUrl + "/accounts/customer/{id}", customerId)
                .retrieve()
                .bodyToFlux(Account.class)
                .collectList();
    }

    private Mono<List<Credit>> getCreditsByCustomer(String customerId) {
        return webClient.get()
                .uri(creditServiceUrl + "/credits/customer/{id}", customerId)
                .retrieve()
                .bodyToFlux(Credit.class)
                .collectList();
    }

    private Mono<BalanceSummary> calculateAverageBalanceForAccount(Account account) {
        return webClient.get()
                .uri(movementServiceUrl + "/movements/account/{id}/daily-balances", account.getId())
                .retrieve()
                .bodyToFlux(DailyBalance.class)
                .map(DailyBalance::getBalance)
                .collectList()
                .flatMap(balances -> {
                    if (balances.isEmpty()) {
                        return Mono.just(new BalanceSummary(account.getId(), "ACCOUNT", 0.0));
                    }
                    double average = balances.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                    return Mono.just(new BalanceSummary(account.getId(), "ACCOUNT", average));
                });
    }

    private Mono<BalanceSummary> calculateAverageBalanceForCredit(Credit credit) {
        return webClient.get()
                .uri(movementServiceUrl + "/movements/credit/{id}/daily-balances", credit.getId())
                .retrieve()
                .bodyToFlux(DailyBalance.class)
                .map(DailyBalance::getBalance)
                .collectList()
                .flatMap(balances -> {
                    if (balances.isEmpty()) {
                        return Mono.just(new BalanceSummary(credit.getId(), "CREDIT", 0.0));
                    }
                    double average = balances.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                    return Mono.just(new BalanceSummary(credit.getId(), "CREDIT", average));
                });
    }
}
