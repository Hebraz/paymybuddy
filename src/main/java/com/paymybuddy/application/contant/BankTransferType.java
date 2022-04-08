package com.paymybuddy.application.contant;

public enum BankTransferType {
    CREDIT_MYBUDDY_ACCOUNT("Credit MyBuddy account"),
    DEBIT_MYBUDDY_ACCOUNT("Debit MyBuddy account");

    private String description;

    BankTransferType(String description){this.description = description;}
}

