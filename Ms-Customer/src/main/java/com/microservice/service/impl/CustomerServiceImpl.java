package com.microservice.service.impl;

import com.microservice.model.Customer;
import com.microservice.repository.CustomerRepository;
import com.microservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;


    @Override
    public Flux<Customer> getAllCustomer() {
        return customerRepository.findAll();
    }

    @Override
    public Mono<Customer> getByIdCustomer(String id) {
        return customerRepository.findById(id);
    }

    @Override
    public Mono<Customer> createCustomer(Customer customer) {
        String typeCustomer = customer.getTypeCustomer();
        switch (typeCustomer){
            case "Personal":
                customer.setTypeProfile("VIP");
                break;
            case "Empresa":
                customer.setTypeProfile("PYME");
                break;
        }
        return customerRepository.save(customer);
    }

    @Override
    public Mono<Customer> updateCustomer(String id, Customer customer) {
        String typeCustomer = customer.getTypeCustomer();
        switch (typeCustomer){
            case "Personal":
                customer.setTypeProfile("VIP");
                break;
            case "Empresa":
                customer.setTypeProfile("PYME");
                break;
        }
        return customerRepository.findById(id).flatMap(newCustomer -> {
            newCustomer.setFullName(customer.getFullName());
            newCustomer.setNumberIdentity(customer.getNumberIdentity());
            newCustomer.setTypeCustomer(customer.getTypeCustomer());
            newCustomer.setTypeProfile(customer.getTypeProfile());
            newCustomer.setPhoneNumber(customer.getPhoneNumber());
            newCustomer.setEmailCustomer(customer.getEmailCustomer());
            return customerRepository.save(newCustomer);
        }).switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> deleteCustomer(String id) {
        return customerRepository.findById(id).flatMap(customer -> customerRepository.deleteById(customer.getId()));
    }
}
