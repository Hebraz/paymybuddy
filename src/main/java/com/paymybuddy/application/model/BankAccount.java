package com.paymybuddy.application.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Bank account entity
 */
@Entity
@Getter
@Setter
@Table(name = "bank_account")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique=true)
    @Pattern(regexp = "^FR[0-9]{23}$", message = "IBAN format must be: 'FR' followed by 23 digits")
    private String iban;

    @Size(min=2,max=25, message = "Bank description must be from 2 to 25 characters in length")
    private String description;

    /*Unidirectional mapping,
    deleting a bank account is not authorized by DB when
    at least a bank transfer is recorded with this bank account*/
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE} , orphanRemoval = true)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private List<BankTransfer> bankTransfers;

    public BankAccount(String description, String iban) {
        this();
        this.iban = iban;
        this.description = description;
    }
    public BankAccount(){
        bankTransfers = new ArrayList<>();
    }
}
