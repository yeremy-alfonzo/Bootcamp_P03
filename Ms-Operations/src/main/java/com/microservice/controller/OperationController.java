package com.microservice.controller;

import com.microservice.model.Deposit;
import com.microservice.model.Withdraw;
import com.microservice.service.DepositService;
import com.microservice.service.WithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/operation")
public class OperationController {

    @Autowired
    WithdrawService withdrawService;

    @Autowired
    DepositService depositService;

    @GetMapping("/withdraw/allWithdraw")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Withdraw> getAllWithdraw(){
        System.out.println("Listar todos los retiros de las cuentas.");
        return withdrawService.getAllWithdraw();
    }

    @PostMapping("/withdraw/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Withdraw> createWithdraw(@RequestBody Withdraw withdraw){
        System.out.println("Retiro creada con Éxito.");
        return withdrawService.createWithdraw(withdraw);
    }

    @GetMapping("/withdraw/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Withdraw>getByIdWithdraw(@PathVariable String id){
        System.out.println("Listar retiros por ID.");
        return withdrawService.getByIdWithdraw(id);
    }


    @GetMapping("/deposit/allDeposit")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Deposit>getAllDeposit(){
        System.out.println("Listar todos los depositos de las cuentas.");
        return depositService.getAllDeposit();
    }

    @PostMapping("/deposit/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Deposit> createDeposit(@RequestBody Deposit deposit){
        System.out.println("Deposito creada con Éxito.");
        return depositService.createDeposit(deposit);
    }

    @GetMapping("/deposit/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Deposit>getByIdDeposit(@PathVariable String id){
        System.out.println("Listar depositos por ID.");
        return depositService.getByIdDeposit(id);
    }

}
