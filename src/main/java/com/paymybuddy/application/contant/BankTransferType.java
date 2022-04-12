package com.paymybuddy.application.contant;

import lombok.Getter;

@Getter
public enum BankTransferType {
    CREDIT_MYBUDDY_ACCOUNT("Credit MyBuddy account"),
    DEBIT_MYBUDDY_ACCOUNT("Debit MyBuddy account");

    private final String description;

    BankTransferType(String description){this.description = description;}
}

