package com.paymybuddy.application.controller.principalInfo;

import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.AuthorityUtils;
@ExtendWith(MockitoExtension.class)
class UsernamePasswordPrincipalInfoTest {

    @Mock
    private UsernamePasswordAuthenticationToken authToken;
    @Mock
    private User userWithNullEmail;

    @BeforeEach
    void initializeTest() throws PrincipalAuthenticationException {

    }

    private User getNominalUser(){
        return new User("pierre.paul.oc@gmail.com","uiouio",AuthorityUtils.createAuthorityList("USER"));
    }

    @Test
    void authenticationType() throws PrincipalAuthenticationException{
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(getNominalUser());
        UsernamePasswordPrincipalInfo usernamePasswordPrincipalInfo = new UsernamePasswordPrincipalInfo(authToken);
        //ACT
        PrincipalInfo.AuthenticationType type = usernamePasswordPrincipalInfo.authenticationType();
        //CHECK
        assertThat(type).isEqualTo(PrincipalInfo.AuthenticationType.USERNAME_PASSWORD );
    }

    @Test
    void UsernamePasswordPrincipalInfoNotAuthenticatedThrowException() throws PrincipalAuthenticationException{
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(false);

        assertThrows(PrincipalAuthenticationException.class, () -> new UsernamePasswordPrincipalInfo(authToken));
    }


    @Test
    void UsernamePasswordPrincipalInfoNullEmailThrowException() throws PrincipalAuthenticationException{
        //PREPARE
        when(userWithNullEmail.getUsername()).thenReturn(null);
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(userWithNullEmail);

        assertThrows(PrincipalAuthenticationException.class, () -> new UsernamePasswordPrincipalInfo(authToken));
    }

    @Test
    void UsernamePasswordPrincipalInfoNominal() throws PrincipalAuthenticationException {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(getNominalUser());
         //ACT
        UsernamePasswordPrincipalInfo usernamePasswordPrincipalInfo = new UsernamePasswordPrincipalInfo(authToken);
        //CHECK
        assertThat(usernamePasswordPrincipalInfo.getEmail()).isEqualTo("pierre.paul.oc@gmail.com");
        assertNull(usernamePasswordPrincipalInfo.getFirstName());
        assertNull(usernamePasswordPrincipalInfo.getLastName());
    }
}