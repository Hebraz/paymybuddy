package com.paymybuddy.application.service;

import com.paymybuddy.application.contant.BankTransferType;
import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.model.BankTransfer;
import com.paymybuddy.application.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
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
        bankAccountService = new BankAccountServiceImpl(bankAccountRepository);
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
    void saveBankAccountNonexistent() {
        //PREPARE
        BankAccount bankAccount = new BankAccount("BNP", "FR89789456456456489"); // id is null
        assertThrows(IllegalArgumentException.class, () -> bankAccountService.updateBankAccount(bankAccount, ""));
    }

    @Test
    void saveBankAccountNominal() throws ForbiddenOperationException {
        String principalEmail = "toto@tata.com";
        int id = 1;
        BankAccount bankAccount = new BankAccount("BNP", "FR89789456456456489");
        bankAccount.setId(id);

        //PREPARE
        when(bankAccountRepository.findByIdAndByUserEmail(id, principalEmail)).thenReturn(Optional.of(new BankAccount()));
        //ACT
        BankAccount  returnedBankAccount = bankAccountService.updateBankAccount(bankAccount, principalEmail);
        //CHECK
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }

    @Test
    void saveBankAccountOwnByANotherOne() throws ForbiddenOperationException {
        String principalEmail = "toto@tata.com";
        int id = 1;
        BankAccount bankAccount = new BankAccount("BNP", "FR89789456456456489");
        bankAccount.setId(id);

        //PREPARE
        when(bankAccountRepository.findByIdAndByUserEmail(id, principalEmail)).thenReturn(Optional.empty());
        //ACT
        assertThrows(ForbiddenOperationException.class, () -> bankAccountService.updateBankAccount(bankAccount, principalEmail));
        //CHECK
        verify(bankAccountRepository, never()).save(bankAccount);
    }

    @Test
    void deleteBankAccountOwnedByUser() throws ForbiddenOperationException {
        String userEmail = "toto@tata.com";
        int id = 49489;
        //PREPARE
        when(bankAccountRepository.findByIdAndByUserEmail(id, userEmail)).thenReturn(Optional.of(new BankAccount()));
        //ACT
        bankAccountService.deleteBankAccount(id,userEmail);
        //CHECK
        verify(bankAccountRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteBankAccountNotOwnedByUser() {
        String userEmail = "toto@tata.com";
        int id = 49489;
        //PREPARE
        when(bankAccountRepository.findByIdAndByUserEmail(id, userEmail)).thenReturn(Optional.empty());

        //ACT
        assertThrows(ForbiddenOperationException.class, () -> bankAccountService.deleteBankAccount(id, userEmail));
    }

    @Test
    void addTransferAccountNotFoundThrowException() {
        BankTransferDto bankTransferDto = new BankTransferDto(BankTransferType.DEBIT_MYBUDDY_ACCOUNT,1, BigDecimal.ZERO);

        //PREPARE
        when(bankAccountRepository.findById(1)).thenReturn(Optional.empty());

        //ACT
        assertThrows(NotFoundException.class, () -> bankAccountService.addTransfer(bankTransferDto));
    }

    @Test
    void addTransferAccountNominal() throws NotFoundException {

        ArgumentCaptor<BankAccount> bankAccountArgumentCaptor = ArgumentCaptor.forClass(BankAccount.class);

        BankTransferDto bankTransferDto = new BankTransferDto(BankTransferType.DEBIT_MYBUDDY_ACCOUNT,1, new BigDecimal("999999999999999.99"));
        BankAccount bankAccountInDatabase = new BankAccount("Desc","FR123456");

        //PREPARE
        when(bankAccountRepository.findById(1)).thenReturn(Optional.of(bankAccountInDatabase));

        //ACT
        Instant expectedTransferDate = Instant.now();
        bankAccountService.addTransfer(bankTransferDto);

        //VERIFY
        verify(bankAccountRepository, times(1)).save(bankAccountArgumentCaptor.capture());
        BankAccount savedBankAccount = bankAccountArgumentCaptor.getValue();
        BankTransfer savedBankTransfer = savedBankAccount.getBankTransfers().get(0);

        assertThat(savedBankTransfer.getDate()).isBetween(expectedTransferDate, expectedTransferDate.plusSeconds(1));
        assertThat(savedBankTransfer.getAmount()).isEqualTo(99999999999999999L);
        assertThat(savedBankTransfer.getDescription()).isEqualTo(BankTransferType.DEBIT_MYBUDDY_ACCOUNT.toString());
    }
}