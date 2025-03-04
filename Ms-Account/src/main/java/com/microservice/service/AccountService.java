package com.microservice.service;

import com.microservice.exception.BusinessException;
import com.microservice.model.Account;
import com.microservice.repository.AccountRepository;
import com.microservice.service.client.CustomerClient;
import com.microservice.service.impl.AccountServiceImpl;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountService implements AccountServiceImpl {

    private  final AccountRepository accountRepository;
    private final CustomerClient customerClient;

    @Override
    public Flux<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Mono<Account> getByIdAccount(String id) {
        return accountRepository.findById(id);
    }

    @Override
    public Mono<Account> createAccount(Account account) {
        account.setStatusAccount("Activo");
        return customerClient.getCustomerById(account.getIdCustomer())
                .flatMap(customer -> {
                    if ("EMPRESARIAL".equalsIgnoreCase(customer.getTypeCustomer())) {
                        if ("AHORRO".equalsIgnoreCase(account.getTypeAccount()) || "PLAZO_FIJO".equalsIgnoreCase(account.getTypeAccount())) {
                            return Mono.error(new BusinessException("Cliente empresarial no puede tener cuentas de ahorro ni a plazo fijo."));
                        }

                        if (account.getHolders() == null || account.getHolders().isEmpty()) {
                            return Mono.error(new BusinessException("Las cuentas empresariales deben tener al menos un titular."));
                        }

                        if (account.getAvailableBalanceAccount() < account.getMinimumOpeningBalance()) {
                            return Mono.error(new BusinessException("El saldo inicial no puede ser menor al monto mínimo de apertura."));
                        }

                        return accountRepository.save(account);
                    }

                    if ("PERSONAL".equalsIgnoreCase(customer.getTypeCustomer())) {
                       return accountRepository.findByIdCustomer(account.getIdCustomer())
                                .collectList()
                                .flatMap(existingAccounts -> {
                                    long savingsCount = existingAccounts.stream()
                                            .filter(acc -> "AHORRO".equalsIgnoreCase(acc.getTypeAccount()))
                                            .count();
                                    long checkingCount = existingAccounts.stream()
                                            .filter(acc -> "CORRIENTE".equalsIgnoreCase(acc.getTypeAccount()))
                                            .count();

                                    if ((account.getTypeAccount().equalsIgnoreCase("AHORRO") && savingsCount >= 1) ||
                                            (account.getTypeAccount().equalsIgnoreCase("CORRIENTE") && checkingCount >= 1)) {
                                        return Mono.error(new BusinessException("Cliente personal ya tiene el límite de cuentas permitidas."));
                                    }

                                    if (account.getAvailableBalanceAccount() < account.getMinimumOpeningBalance()) {
                                        return Mono.error(new BusinessException("El saldo inicial no puede ser menor al monto mínimo de apertura."));
                                    }

                                    return accountRepository.save(account);
                                });
                    }
                    if (account.getAvailableBalanceAccount() < account.getMinimumOpeningBalance()) {
                        return Mono.error(new BusinessException("El saldo inicial no puede ser menor al monto mínimo de apertura."));
                    }

                    return Mono.error(new BusinessException("Tipo de cliente no válido."));
                });
    }

    @Override
    public Mono<Account> updateAccount(String id, Account account) {
        return accountRepository.findById(id).flatMap(account1 -> {
            account1.setAvailableBalanceAccount(account.getAvailableBalanceAccount());
            account1.setStatusAccount(account.getStatusAccount());
            return accountRepository.save(account1);
        }).switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> deleteAccount(String id) {
        return accountRepository.findById(id).flatMap(account -> accountRepository.deleteById(account.getId()));
    }

    @Override
    public Mono<Double> getAccountBalance(String accountId) {
        return accountRepository.findById(accountId)
                .map(Account::getAvailableBalanceAccount)
                .switchIfEmpty(Mono.error(new BusinessException("Cuenta no encontrada")));
    }
}
