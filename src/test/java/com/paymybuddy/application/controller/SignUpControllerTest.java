package com.paymybuddy.application.controller;

import com.paymybuddy.application.dto.ConnectionDto;
import com.paymybuddy.application.dto.SignUpDto;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class SignUpControllerTest {

    @Mock
    UserService userService;
    @Mock
    Model model;
    @Mock
    HttpServletRequest request;

    @Autowired
    SignUpController signUpController;

    @BeforeEach
    void initializeTest(){
        signUpController = new SignUpController(userService);
    }

    @Test
    void showSignUp() {
        //PREPARE
        ArgumentCaptor<SignUpDto> SignUpDtoArgumentCaptor = ArgumentCaptor.forClass(SignUpDto.class);
        //ACT
        String page = signUpController.showSignUp(model);
        //CHECK
        verify(model, times(1)).addAttribute(eq("signUpDto"), SignUpDtoArgumentCaptor.capture());
        assertNotNull(SignUpDtoArgumentCaptor.getValue());
        assertThat(page).isEqualTo("signUp");
    }

    @Test
    void createAccountAuthenticationOk() throws ForbiddenOperationException, ServletException {
        SignUpDto signUpDto = new SignUpDto("email@email.com", "email@email.com", "pwd", "Pierre", "Paul");
        //ACT
        String page = signUpController.createAccount(request,model,signUpDto);
        //CHECK
        verify(userService,times(1)).createUserAccount(signUpDto);
        verify(request,times(1)).login("email@email.com","pwd");
        assertThat(page).isEqualTo("redirect:/home");
    }

    @Test
    void createAccountAuthenticationFailed() throws ServletException, ForbiddenOperationException {
        //PREPARE
        SignUpDto signUpDto = new SignUpDto("email@email.com", "email@email.com", "pwd", "Pierre", "Paul");
        doThrow(ServletException.class).when(request).login(any(),any());
        //ACT
        String page = signUpController.createAccount(request,model,signUpDto);
        //CHECK
        verify(userService,times(1)).createUserAccount(signUpDto);
        verify(request,times(1)).login("email@email.com","pwd");
        assertThat(page).isEqualTo("redirect:/login");
    }
}