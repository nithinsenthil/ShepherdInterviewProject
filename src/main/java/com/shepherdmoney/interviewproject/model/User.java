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
@Table(name = "MyUser")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    private String email;

    private List<Integer> creditCardIdList;

    public void appendCreditCardIdList(Integer cardId) {
        this.creditCardIdList.add(cardId);
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.creditCardIdList = new ArrayList<>();
    }
}
