package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.service.TransactionService;
import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableTransactionManagement
public class UserController {

    // NOTE: All API endpoints are directed to the TransactionService class to abstract business logic from the handling of http requests

    private final TransactionService transactionService;

    @Autowired
    public UserController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PutMapping("/user")
    public ResponseEntity<Integer> createUser(@RequestBody CreateUserPayload payload) {
        return transactionService.saveUserInTransaction(payload);
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@RequestParam int userId) {
        return transactionService.deleteUserInTransaction(userId);
    }
}
