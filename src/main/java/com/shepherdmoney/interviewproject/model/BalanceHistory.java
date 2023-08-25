package com.shepherdmoney.interviewproject.model;

import java.time.Instant;

import com.shepherdmoney.interviewproject.controller.CreditCardController;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class BalanceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private Instant date;

    private double balance;

    public BalanceHistory(Instant date, double balance) {
        this.date = date;
        this.balance = balance;
    }
}
