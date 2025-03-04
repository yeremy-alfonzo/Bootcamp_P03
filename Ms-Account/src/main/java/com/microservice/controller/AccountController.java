package com.microservice.controller;

import com.microservice.exception.BusinessException;
import com.microservice.model.Account;
import com.microservice.model.BalanceSummary;
import com.microservice.model.PaymentRequest;
import com.microservice.service.AccountService;
import com.microservice.service.DebitCardService;
import com.microservice.service.impl.ReportService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController{

    private final AccountService accountService;
    private final ReportService reportService;
    private final DebitCardService debitCardService;

    private static final String ACCOUNT = "account";

    @GetMapping(value = "/allAccounts")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Account> getAllAccounts(){
        System.out.println("Listar todas las cuentas bancarias.");
        return accountService.getAllAccounts();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Account> getAccountById(@PathVariable String id){
        System.out.println("Buscar cuenta bancaria por ID.");
        return accountService.getByIdAccount(id);
    }

    @PostMapping(value = "/create")
    @CircuitBreaker(name = ACCOUNT, fallbackMethod = "account")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Account> createAccount(@RequestBody Account account){
        System.out.println("Cuenta bancaria creada con Éxito.");
        return accountService.createAccount(account);
    }

    @PutMapping(value = "/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = ACCOUNT, fallbackMethod = "account")
    public Mono<Account> updateAccount(@PathVariable String id, @RequestBody Account account){
        System.out.println("Cuenta bancaria actualizada con Éxito.");
        return accountService.updateAccount(id, account);
    }

    @DeleteMapping(value = "/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteAccount(@PathVariable String id){
        System.out.println("Cuenta bancaria eliminada.");
        return accountService.deleteAccount(id);
    }
    @GetMapping("/{id}/balance")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Double>> getAccountBalance(@PathVariable String id) {
        return accountService.getAccountBalance(id)
                .map(balance -> ResponseEntity.ok(balance))
                .onErrorResume(BusinessException.class, e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/balance-summary/{customerId}")
    public Mono<ResponseEntity<List<BalanceSummary>>> getBalanceSummary(@PathVariable String customerId) {
        return reportService.generateBalanceSummary(customerId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @PostMapping("/pay")
    public Mono<ResponseEntity<String>> payWithDebitCard(@RequestBody PaymentRequest paymentRequest) {
        return debitCardService.payWithDebitCard(paymentRequest)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PostMapping("/{cardId}/associate-all-accounts")
    public Mono<ResponseEntity<String>> associateAllAccounts(@PathVariable String cardId) {
        return debitCardService.associateAllAccountsToCard(cardId)
                .then(Mono.just(ResponseEntity.ok("Tarjeta asociada a todas las cuentas del cliente.")))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PutMapping("/{cardId}/set-primary-account/{accountId}")
    public Mono<ResponseEntity<String>> setPrimaryAccount(@PathVariable String cardId, @PathVariable String accountId) {
        return debitCardService.setPrimaryAccount(cardId, accountId)
                .then(Mono.just(ResponseEntity.ok("Cuenta principal asignada correctamente.")))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PostMapping("/{debitCardId}/transactions")
    public Mono<ResponseEntity<String>> makePayment(@PathVariable String debitCardId, @RequestBody PaymentRequest request) {
        return debitCardService.processDebitTransaction(debitCardId, request.getAmount())
                .then(Mono.just(ResponseEntity.ok("Pago realizado con éxito.")))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @GetMapping("/{cardNumber}/balance")
    public Mono<ResponseEntity<BigDecimal>> getPrimaryAccountBalance(@PathVariable String cardNumber) {
        return debitCardService.getPrimaryAccountBalance(cardNumber)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(BigDecimal.ZERO)));
    }
}
