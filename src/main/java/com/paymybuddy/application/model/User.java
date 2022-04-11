package com.paymybuddy.application.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.StandardException;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * User entity
 */
@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique=true)
    @Email
    @Size(max=100, message = "Email must be at most 100 characters in length")
    private String email;
    @Size(max=100, message = "Password must be at most 100 characters in length")
    private String password;

    private boolean enabled;

    @Column(name = "first_name")
    @Size(max=50, message = "First name must be at most 50 characters in length")
    private String firstName;

    @Column(name = "last_name")
    @Size(max=50, message = "Last name must be at most 50 characters in length")
    private String lastName;

    private long balance; /*unit is cent*/

    /*Bidirectional mapping,
    deleting user shall not delete the associated transactions as payer,
    as these transactions may be consulted by the credit user*/
    @OneToMany( mappedBy = "payer", cascade = {CascadeType.PERSIST, CascadeType.MERGE })
    private List<Transaction> transactionsAsPayer;

    /*Bidirectional mapping,
    deleting user shall not delete the associated transactions as credit,
    as these transactions may be consulted by the payer user*/
    @OneToMany( mappedBy = "credit", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Transaction> transactionsAsCredit;

    /*Unidirectional mapping,
    deleting user shall not delete connections*/
    @ManyToMany( fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH} )
    @JoinTable(
            name = "user_connection_assoc",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "connection_id")
    )
    private List<User> connections;

    /*Unidirectional mapping,
    deleting user shall delete all associated bank accounts*/
    @OneToMany(fetch = FetchType.LAZY,   cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false)
    private List<BankAccount> bankAccounts;

    /*Unidirectional mapping,
     deleting user shall not delete associated authority*/
    @ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="authority_id", referencedColumnName = "id")
    private Authority authority;

    public User(){
        connections = new ArrayList<>();
        transactionsAsCredit = new ArrayList<>();
        transactionsAsPayer = new ArrayList<>();
        bankAccounts = new ArrayList<>();
        this.enabled = true;
    }
    public User(String email, String password, String firstName, String lastName, long balance) {
        this();
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;
    }

    public void addTransactionAsPayer(Transaction transaction){
        transaction.setPayer(this);
        transactionsAsPayer.add(transaction);
    }

    public void addTransactionAsCredit(Transaction transaction){
        transaction.setCredit(this);
        transactionsAsCredit.add(transaction);
    }
}

