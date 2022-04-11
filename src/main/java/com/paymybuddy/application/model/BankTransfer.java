package com.paymybuddy.application.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "bank_transfer")
public class BankTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private Instant date;

    private long amount; /*unit is cent*/

    private String description;


    public BankTransfer(){}

    public BankTransfer(Instant date, long amount, String description) {
        this.date = date;
        this.amount = amount;
        this.description = description;
    }
}
