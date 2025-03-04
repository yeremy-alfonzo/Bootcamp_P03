package com.microservice.service.client;

import com.microservice.model.Account;
import com.microservice.model.Credit;
import com.microservice.model.Customer;
import com.microservice.model.CustomerSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CustomerSummaryClient {

    private WebClient webClient;

    @Value("${http://localhost:8090}")
    private String customerServiceUrl;

    @Value("${http://localhost:8080}")
    private String accountServiceUrl;

    @Value("${http://localhost:8085}")
    private String creditServiceUrl;


    public Mono<CustomerSummary> getCustomerSummary(String customerId) {
        Mono<Customer> customerMono = webClient.get()
                .uri(customerServiceUrl + "/customers/{id}", customerId)
                .retrieve()
                .bodyToMono(Customer.class);

        Mono<List<Account>> accountsMono = webClient.get()
                .uri(accountServiceUrl + "/accounts/customer/{id}", customerId)
                .retrieve()
                .bodyToFlux(Account.class)
                .collectList();

        Mono<List<Credit>> creditsMono = webClient.get()
                .uri(creditServiceUrl + "/credits/customer/{id}", customerId)
                .retrieve()
                .bodyToFlux(Credit.class)
                .collectList();

        return Mono.zip(customerMono, accountsMono, creditsMono)
                .map(tuple -> new CustomerSummary(
                        tuple.getT1(),
                        tuple.getT2(),
                        tuple.getT3()
                ));
    }
}
