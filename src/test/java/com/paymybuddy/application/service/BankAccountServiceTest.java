package com.paymybuddy.application.service;

import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    BankAccountRepository bankAccountRepository;

    private BankAccountService bankAccountService;
    @BeforeEach
    void initializeTest(){
        bankAccountService = new BankAccountService(bankAccountRepository);
    }
    @Test
    void getByIdEmpty() {
        //PREPARE
        when(bankAccountRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        //ACT
        assertThrows(NotFoundException.class, () -> bankAccountService.getById(any(Integer.class)));
    }

    @Test
    void getByIdPresent() throws NotFoundException {
        //PREPARE
        BankAccount bankAccount = new BankAccount("BNP", "FR89789456456456489");
        when(bankAccountRepository.findById(any(Integer.class))).thenReturn(Optional.of(bankAccount));

        //ACT
        BankAccount  returnedBankAccount = bankAccountService.getById(7788);

        //CHECK
        verify(bankAccountRepository, times(1)).findById(7788);
        assertThat(returnedBankAccount).isEqualTo(bankAccount);
    }

    @Test
    void saveBankAccount() {
        //PREPARE
        BankAccount bankAccount = new BankAccount("BNP", "FR89789456456456489");

        //ACT
        BankAccount  returnedBankAccount = bankAccountService.saveBankAccount(bankAccount);

        //CHECK
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }

    @Test
    void deleteBankAccount() {
        //ACT
        bankAccountService.deleteBankAccount(49489);

        //CHECK
        verify(bankAccountRepository, times(1)).deleteById(49489);
    }
}