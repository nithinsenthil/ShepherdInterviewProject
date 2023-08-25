package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.service.TransactionService;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@EnableTransactionManagement
public class CreditCardController {

    // NOTE: All API endpoints are delegated to the TransactionService class to abstract business logic from the handling of http requests

    private TransactionService transactionService;
    @Autowired
    public CreditCardController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/credit-card")
    public ResponseEntity<Integer> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        return transactionService.saveCardInTransaction(payload);
    }

    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        return transactionService.getAllCardOfUserInTransaction(userId);
    }

    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        return transactionService.getUserIdForCreditCardInTransaction(creditCardNumber);
    }

    @PostMapping("/credit-card:update-balance")
    public ResponseEntity<Integer> postMethodName(@RequestBody UpdateBalancePayload[] payload) {
        return transactionService.updateBalanceInTransaction(payload);
    }
}
