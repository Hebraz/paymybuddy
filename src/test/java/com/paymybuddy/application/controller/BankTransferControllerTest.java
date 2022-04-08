package com.paymybuddy.application.controller;

import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankTransferControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private PrincipalInfoFactory principalInfoFactory;
    @Mock
    PrincipalInfo principalInfo;
    @Mock
    HttpServletRequest httpServletRequest;

    private BankTransferController bankTransferController;

    @BeforeEach
    void initializeTest(){
        bankTransferController = new BankTransferController(userService,principalInfoFactory);
    }

    @Test
    void executeBankTransfer() throws PrincipalAuthenticationException, NotFoundException, ForbiddenOperationException {
        //prepare

        when(principalInfo.getEmail()).thenReturn("pierre.paul.oc@gmail.com");
        when(principalInfoFactory.getPrincipalInfo(any())).thenReturn(principalInfo);
        BankTransferDto bankTransferDto = new BankTransferDto();


        //ACT
        String returnedPage = bankTransferController.executeBankTransfer(httpServletRequest, bankTransferDto);

        //CHECK
        verify(userService,times(1)).executeBankTransfer("pierre.paul.oc@gmail.com", bankTransferDto);
        assertThat(returnedPage).isEqualTo("redirect:/profile");
    }
}