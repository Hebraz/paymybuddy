package com.paymybuddy.application.controller;

import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.service.BankAccountService;
import com.paymybuddy.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private BankAccountService bankAccountService;
    @Mock
    private PrincipalInfoFactory principalInfoFactory;
    @Mock
    PrincipalInfo principalInfo;
    @Mock
    Principal principal;
    @Mock
    Model model;
    @Mock
    HttpServletRequest httpServletRequest;

    private ProfileController profileController;

    @BeforeEach
    void initializeTest(){
        profileController = new ProfileController(principalInfoFactory, userService,bankAccountService);
    }

    @Test
    void showProfileForm() throws PrincipalAuthenticationException {
        //PREPARE
        String email = "pierre.paul.oc@gmail.com";
        User userReturnedByService = new User();
        when(principalInfoFactory.getPrincipalInfo(principal)).thenReturn(principalInfo);
        when(principalInfo.getEmail()).thenReturn(email);
        when(userService.getPrincipalByEmail(email)).thenReturn(userReturnedByService);

        //ACT
        String returnedPage = profileController.showProfileForm(principal, model);

        //VERIFY
        verify(userService, times(1)).getPrincipalByEmail(email);
        verify(model, times(1)).addAttribute("user", userReturnedByService);
        verify(model, times(1)).addAttribute(eq("bankAccount"), any(BankAccount.class));
        verify(model, times(1)).addAttribute(eq("bankTransferDto"), any(BankTransferDto.class));

        assertThat(returnedPage).isEqualTo("profile");
    }
}