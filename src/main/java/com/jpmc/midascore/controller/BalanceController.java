package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class BalanceController {
    private final UserRepository repository;
    public BalanceController(UserRepository repository){
        this.repository = repository;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam(name="userId") Long userId){
       UserRecord record = repository.findById(userId).orElse(null);
       float balance = record != null ? record.getBalance() : 0.0f;
       return new Balance(balance);
    }
}
