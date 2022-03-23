package com.paymybuddy.application.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
public class BankTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private Instant date;

    private long amount; /*unit is cent*/

    private String description;
}
