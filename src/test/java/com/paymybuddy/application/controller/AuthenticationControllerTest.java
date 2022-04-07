package com.paymybuddy.application.controller;

import com.paymybuddy.application.controller.principalInfo.OAuth2PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.service.UserService;
import jdk.jfr.MetadataDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.anyOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PrincipalInfoFactory principalInfoFactory;

    @Mock
    private Principal principal;

    @Mock
    private PrincipalInfo principalInfo;

    @Mock
    private Model model;

    private AuthenticationController authenticationController;

    @BeforeEach
    void initializeTest(){
        authenticationController = new AuthenticationController(userService, principalInfoFactory);
    }

    @Test
    void showLoginForm() {
        String page_name = authenticationController.showLoginForm();
        assertThat(page_name).isEqualTo("login");
    }

    @Test
    void showHomeOAuth2FirstConnection() throws Exception {
        //PREPARE
        final String email = "pierre.paul.oc@gmail.com";
        final String firstName = "Pierre";
        final String lastName = "Paul";

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(principalInfoFactory.getPrincipalInfo(any())).thenReturn(principalInfo);
        when(principalInfo.authenticationType()).thenReturn(PrincipalInfo.AuthenticationType.OAUTH2);
        when(principalInfo.getEmail()).thenReturn(email);
        when(principalInfo.getFirstName()).thenReturn(firstName);
        when(principalInfo.getLastName()).thenReturn(lastName);
        when(userService.findByEmail(any())).thenReturn(Optional.empty());
        when(userService.saveUser(any())).thenReturn(new User(email,"",firstName,lastName,0));

        //ACT
        authenticationController.showHome(principal,model);

        //VERIFY
        verify(userService, times(1)).findByEmail(email);
        verify(userService, times(1)).saveUser(userCaptor.capture());
        assertThat(userCaptor.getValue())
                .extracting(User::getEmail,
                        User::getFirstName,
                        User::getLastName)
                .containsExactly(email,firstName,lastName);
        verify(model, times(1)).addAttribute(any(), userCaptor.capture());
        assertThat(userCaptor.getValue())
                .extracting(User::getEmail,
                        User::getFirstName,
                        User::getLastName)
                .containsExactly(email,firstName,lastName);
    }


    @Test
    void showHomeOAuth2NotFirstConnection() throws Exception {
        //PREPARE
        final String email = "pierre.paul.oc@gmail.com";
        final String firstName = "Pierre";
        final String lastName = "Paul";

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(principalInfoFactory.getPrincipalInfo(any())).thenReturn(principalInfo);
        when(principalInfo.authenticationType()).thenReturn(PrincipalInfo.AuthenticationType.OAUTH2);
        when(principalInfo.getEmail()).thenReturn(email);
        when(userService.findByEmail(any())).thenReturn(Optional.of(new User(email,"",firstName,lastName,0)));

        //ACT
        authenticationController.showHome(principal,model);

        //VERIFY
        verify(userService, times(1)).findByEmail(email);
        verify(userService, never()).saveUser(userCaptor.capture());
        verify(model, times(1)).addAttribute(any(), userCaptor.capture());
        assertThat(userCaptor.getValue())
                .extracting(User::getEmail,
                        User::getFirstName,
                        User::getLastName)
                .containsExactly(email,firstName,lastName);
    }

    @Test
    void showHomeUsernamePasswordAuth() throws Exception {
        //PREPARE
        final String email = "pierre.paul.oc@gmail.com";
        final String firstName = "Pierre";
        final String lastName = "Paul";

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(principalInfoFactory.getPrincipalInfo(any())).thenReturn(principalInfo);
        when(principalInfo.authenticationType()).thenReturn(PrincipalInfo.AuthenticationType.USERNAME_PASSWORD);
        when(principalInfo.getEmail()).thenReturn(email);
        when(userService.findByEmail(any())).thenReturn(Optional.of(new User(email,"",firstName,lastName,0)));

        //ACT
        authenticationController.showHome(principal,model);

        //VERIFY
        verify(userService, times(1)).findByEmail(email);
        verify(userService, never()).saveUser(userCaptor.capture());
        verify(model, times(1)).addAttribute(any(), userCaptor.capture());
        assertThat(userCaptor.getValue())
                .extracting(User::getEmail,
                        User::getFirstName,
                        User::getLastName)
                .containsExactly(email,firstName,lastName);
    }

    @Test
    void showHomeUserNotFoundInDb() throws Exception {
        //PREPARE
        final String email = "pierre.paul.oc@gmail.com";
        final String firstName = "Pierre";
        final String lastName = "Paul";

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(principalInfoFactory.getPrincipalInfo(any())).thenReturn(principalInfo);
        when(principalInfo.authenticationType()).thenReturn(PrincipalInfo.AuthenticationType.USERNAME_PASSWORD);
        when(principalInfo.getEmail()).thenReturn(email);
        when(userService.findByEmail(any())).thenReturn(Optional.empty());

        //ACT
        assertThrows(PrincipalAuthenticationException.class,
                () -> authenticationController.showHome(principal,model));
    }
}