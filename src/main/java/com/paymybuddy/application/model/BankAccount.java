package com.paymybuddy.application.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique=true)
    private String iban;

    private String description;

    /*Unidirectional mapping,
    deleting a bank account is not authorized by DB when
    at least a bank transfer is recorded with this bank account*/
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE} , orphanRemoval = true)
    @JoinColumn(name = "bank_account_id")
    private List<BankTransfer> bankTransfers;
}
