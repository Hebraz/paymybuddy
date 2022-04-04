package com.paymybuddy.application.controller.principalInfo;

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

    private UsernamePasswordPrincipalInfo usernamePasswordPrincipalInfo;

    @BeforeEach
    void initializeTest() {
        usernamePasswordPrincipalInfo = new UsernamePasswordPrincipalInfo(authToken);
    }

    private User getUser(){
        return new User("pierre.paul.oc@gmail.com","uiouio",AuthorityUtils.createAuthorityList("USER"));
    }

    @Test
    void authenticationType() {
        PrincipalInfo.AuthenticationType type = usernamePasswordPrincipalInfo.authenticationType();
        assertThat(type).isEqualTo(PrincipalInfo.AuthenticationType.USERNAME_PASSWORD );
    }

    @Test
    void emailNotAuthenticated() {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(false);
        //ACT
        String email = usernamePasswordPrincipalInfo.getEmail();

        //CHECK
        assertNull(email);
    }

    @Test
    void emailOk() {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(getUser());

        //ACT
        String email = usernamePasswordPrincipalInfo.getEmail();

        //CHECK
        assertThat(email).isEqualTo("pierre.paul.oc@gmail.com");
    }

    @Test
    void firstName() {
        //ACT
        String firstName = usernamePasswordPrincipalInfo.getFirstName();

        //CHECK
        assertNull(firstName);
    }

    @Test
    void lastName() {
        //ACT
        String lastName = usernamePasswordPrincipalInfo.getLastName();

        //CHECK
        assertNull(lastName);
    }
}