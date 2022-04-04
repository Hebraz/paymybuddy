package com.paymybuddy.application.controller.principalInfo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2PrincipalInfoTest {

    @Mock
    private OAuth2AuthenticationToken authToken;

    private OAuth2PrincipalInfo oAuth2PrincipalInfo;

    private DefaultOidcUser defaultOidcUser;

    @BeforeEach
    void initializeTest(){
        oAuth2PrincipalInfo = new OAuth2PrincipalInfo(authToken);

        Map<String, Object> claims = new HashMap<>();
        claims.putIfAbsent("at_hash","dzfazefe");
        claims.putIfAbsent("sub","2575725752");
        claims.putIfAbsent("email_verified","true");
        claims.putIfAbsent("iss","https://accounts.titi.com");
        claims.putIfAbsent("given_name","Pierre");
        claims.putIfAbsent("locale","fr");
        claims.putIfAbsent("nonce","tjtrjrjy");
        claims.putIfAbsent("picture","https://fgjfjfg");
        claims.putIfAbsent("aud","[fhjjg-rtyr.apps.ytut.com]");
        claims.putIfAbsent("azp","gjghj-ytjty.apps.jgj.com");
        claims.putIfAbsent("name","Pierre Paul");
        claims.putIfAbsent("exp","2022-04-04T09:16:32Z");
        claims.putIfAbsent("family_name","Paul");
        claims.putIfAbsent("iat","2022-04-04T08:16:32Z");
        claims.putIfAbsent("email","pierre.paul.oc@gmail.com");
        OidcIdToken oidcIdToken = new OidcIdToken("eryery.eytryterfdgdfhVYGGbw-zerz-eztret-flLN-38387357-4373", Instant.now(), Instant.now().plusSeconds(30), claims);
        defaultOidcUser = new DefaultOidcUser(null, oidcIdToken);

    }

    @Test
    void authenticationType() {
        PrincipalInfo.AuthenticationType type = oAuth2PrincipalInfo.authenticationType();
        assertThat(type).isEqualTo(PrincipalInfo.AuthenticationType.OAUTH2);
    }

    @Test
    void emailNullWhenNotOidcIdToken() {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(null);
        //ACT
        String email = oAuth2PrincipalInfo.getEmail();

        //CHECK
        assertNull(email);
    }

    @Test
    void emailNullWhenNotAuthenticated() {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(false);
        //ACT
        String email = oAuth2PrincipalInfo.getEmail();

        //CHECK
        assertNull(email);
    }

    @Test
    void emailOK() {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(defaultOidcUser);

        //ACT
        String email = oAuth2PrincipalInfo.getEmail();

        //CHECK
        assertThat(email).isEqualTo("pierre.paul.oc@gmail.com");
    }

    @Test
    void firstNameNullWhenNotAuthenticated() {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(false);
        //ACT
        String firstName = oAuth2PrincipalInfo.getFirstName();

        //CHECK
        assertNull(firstName);
    }

    @Test
    void firstNameNullWhenNotOidcIdToken() {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(null);
        //ACT
        String firstName = oAuth2PrincipalInfo.getFirstName();

        //CHECK
        assertNull(firstName);
    }

    @Test
    void firstNameOK() {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(defaultOidcUser);

        //ACT
        String firstName = oAuth2PrincipalInfo.getFirstName();

        //CHECK
        assertThat(firstName).isEqualTo("Pierre");
    }

    @Test
    void lastNameNullWhenNotAuthenticated() {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(false);
        //ACT
        String lastName = oAuth2PrincipalInfo.getLastName();

        //CHECK
        assertNull(lastName);
    }

    @Test
    void lastNameNullWhenNotOidcIdToken() {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(null);
        //ACT
        String lastName = oAuth2PrincipalInfo.getLastName();

        //CHECK
        assertNull(lastName);
    }


    @Test
    void lastNameOK() {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(defaultOidcUser);

        //ACT
        String lastName = oAuth2PrincipalInfo.getLastName();

        //CHECK
        assertThat(lastName).isEqualTo("Paul");
    }
}