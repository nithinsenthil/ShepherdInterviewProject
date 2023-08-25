package com.shepherdmoney.interviewproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String issuanceBank;

    private String number;

    @Column(name = "USERID",nullable = false)
    private int userId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BalanceHistory> balanceHistoryList = new ArrayList<BalanceHistory>();

    public CreditCard(int userId, String issuanceBank, String number) {
        this.userId = userId;
        this.issuanceBank = issuanceBank;
        this.number = number;
    }
}
