package com.paymybuddy.application.controller;

import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.dto.ConnectionDto;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConnectionControllerTest {

    @Mock
    Model model;
    @Mock
    UserService userService;
    @Mock
    PrincipalInfoFactory principalInfoFactory;
    @Mock
    PrincipalInfo principalInfo;
    @Mock
    Principal principal;
    @Mock
    RedirectAttributes redirectAttributes;

    @Autowired
    ConnectionController connectionController;

    @BeforeEach
    void initializeTest(){
        connectionController = new ConnectionController(userService, principalInfoFactory);
    }

    @Test
    void showConnectionAddForm() {
        //PREPARE
        ArgumentCaptor<ConnectionDto> connectionDtoArgumentCaptor = ArgumentCaptor.forClass(ConnectionDto.class);
        //ACT
        String page = connectionController.showConnectionAddForm(model);
        //CHECK
        verify(model, times(1)).addAttribute(eq("connectionDto"), connectionDtoArgumentCaptor.capture());
        assertNotNull(connectionDtoArgumentCaptor.getValue());
        assertThat(page).isEqualTo("connection");


    }

    @Test
    void addConnection() throws PrincipalAuthenticationException, NotFoundException {
        //prepare
        when(principalInfo.getEmail()).thenReturn("pierre.paul.oc@gmail.com");
        when(principalInfoFactory.getPrincipalInfo(any())).thenReturn(principalInfo);
        ConnectionDto connectionDto = new ConnectionDto("mybuddy@gmail.com");
        //ACT
        String returnedPage = connectionController.addConnection(redirectAttributes, principal, connectionDto);

        //CHECK
        verify(userService,times(1)).addConnection("pierre.paul.oc@gmail.com", connectionDto);
        assertThat(returnedPage).isEqualTo("redirect:/transfer");
    }
}