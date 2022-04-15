package com.paymybuddy.application.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
public class TransactionDto {

    @Email
    private String connectionEmail;

    @Digits(integer = 99, fraction = 2, message = "Amount must be expressed in cent")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    @DecimalMax(value = "999999999999999.99", message = "Amount must be les that 1 quadrillion")
    private BigDecimal amount;

    @Size(max=100, message="Email must be at most 100 characters in length")
    private String description;

    public TransactionDto(String connectionEmail, BigDecimal amount) {
        this.connectionEmail = connectionEmail;
        this.amount = amount;
    }

    public TransactionDto(){}
}
