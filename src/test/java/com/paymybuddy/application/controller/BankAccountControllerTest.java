package com.paymybuddy.application.controller;

import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.service.BankAccountService;
import com.paymybuddy.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import java.net.BindException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountControllerTest {

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    PrincipalInfoFactory principalInfoFactory;

    @Mock
    UserService userService;

    @Mock
    BankAccountService bankAccountService;

    @Mock
    Model model;

    @Mock
    PrincipalInfo principalInfo;

    private BankAccountController bankAccountController;
    @BeforeEach
    void initializeTest(){
        bankAccountController = new BankAccountController(principalInfoFactory, userService, bankAccountService);
    }

    @Test
    void createBankAccount() throws PrincipalAuthenticationException {
        //prepare
        when(principalInfo.getEmail()).thenReturn("pierre.paul.oc@gmail.com");
        when(principalInfoFactory.getPrincipalInfo(any())).thenReturn(principalInfo);
        when(httpServletRequest.getHeader("Referer")).thenReturn("parent_page");
        BankAccount bankAccount = new BankAccount("BNP","12345678912345678912345");


        //ACT
        String page = bankAccountController.createBankAccount(httpServletRequest,bankAccount);

        //CHECK
        verify(userService,times(1)).addBankAccount("pierre.paul.oc@gmail.com", bankAccount);
        assertThat(page).isEqualTo("redirect:parent_page");
    }

    @Test
    void deleteBankAccount() {
        //prepare
        when(httpServletRequest.getHeader("Referer")).thenReturn("parent_page");

        //ACT
        String page = bankAccountController.deleteBankAccount(httpServletRequest,434);

        //CHECK
        verify(bankAccountService,times(1)).deleteBankAccount(434);
        assertThat(page).isEqualTo("redirect:parent_page");
    }

    @Test
    void showBankAccountEditForm() throws NotFoundException {
        BankAccount bankAccount = new BankAccount("BNP", "FR1234568978945123125");
        when(bankAccountService.getById(any(Integer.class))).thenReturn(bankAccount);

        //ACT
        String page = bankAccountController.showBankAccountEditForm(model, 494591);

        //CHECK
        verify(bankAccountService,times(1)).getById(494591);
        verify(model,times(1)).addAttribute("bankAccount",bankAccount);
        assertThat(page).isEqualTo("bankAccountEdit");
    }

    @Test
    void updateBankAccount() {
        //prepare
        BankAccount bankAccount = new BankAccount("BNP", "FR1234568978945123125");
        when(httpServletRequest.getHeader("Referer")).thenReturn("parent_page");

        //ACT
        String page = bankAccountController.updateBankAccount(httpServletRequest,bankAccount);

        //CHECK
        verify(bankAccountService,times(1)).saveBankAccount(bankAccount);
        assertThat(page).isEqualTo("redirect:parent_page");
    }
}