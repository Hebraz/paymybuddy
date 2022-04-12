package com.paymybuddy.application.dto;

import com.paymybuddy.application.contant.BankTransferType;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Getter
@Setter
public class BankTransferDto {

    private BankTransferType transferType;

    private int bankId;

    @Digits(integer = 99, fraction = 2, message = "Amount must be expressed in cent")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    @DecimalMax(value = "999999999999999.99", message = "Amount must be les that 1 quadrillion")
    private BigDecimal amount;

    public BankTransferDto(BankTransferType transferType, int bankId, BigDecimal amount) {
        this.transferType = transferType;
        this.bankId = bankId;
        this.amount = amount;
    }

    public BankTransferDto(){}
}