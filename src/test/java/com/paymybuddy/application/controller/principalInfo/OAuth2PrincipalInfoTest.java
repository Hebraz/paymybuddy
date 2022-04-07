package com.paymybuddy.application.controller.principalInfo;

import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

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

    private     Map<String, Object> oidcIdTokenNominalClaims;

    @BeforeEach
    void initializeTest() throws PrincipalAuthenticationException {
        oidcIdTokenNominalClaims = new HashMap<>();
        oidcIdTokenNominalClaims.putIfAbsent("at_hash","dzfazefe");
        oidcIdTokenNominalClaims.putIfAbsent("sub","2575725752");
        oidcIdTokenNominalClaims.putIfAbsent("email_verified","true");
        oidcIdTokenNominalClaims.putIfAbsent("iss","https://accounts.titi.com");
        oidcIdTokenNominalClaims.putIfAbsent("given_name","Pierre");
        oidcIdTokenNominalClaims.putIfAbsent("locale","fr");
        oidcIdTokenNominalClaims.putIfAbsent("nonce","tjtrjrjy");
        oidcIdTokenNominalClaims.putIfAbsent("picture","https://fgjfjfg");
        oidcIdTokenNominalClaims.putIfAbsent("aud","[fhjjg-rtyr.apps.ytut.com]");
        oidcIdTokenNominalClaims.putIfAbsent("azp","gjghj-ytjty.apps.jgj.com");
        oidcIdTokenNominalClaims.putIfAbsent("name","Pierre Paul");
        oidcIdTokenNominalClaims.putIfAbsent("exp","2022-04-04T09:16:32Z");
        oidcIdTokenNominalClaims.putIfAbsent("family_name","Paul");
        oidcIdTokenNominalClaims.putIfAbsent("iat","2022-04-04T08:16:32Z");
        oidcIdTokenNominalClaims.putIfAbsent("email","pierre.paul.oc@gmail.com");
    }



    @Test
    void authenticationType() throws PrincipalAuthenticationException {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(getNominalOidcUser());
        OAuth2PrincipalInfo auth2PrincipalInfo = new OAuth2PrincipalInfo(authToken);
        //ACT
        PrincipalInfo.AuthenticationType type = auth2PrincipalInfo.authenticationType();
        assertThat(type).isEqualTo(PrincipalInfo.AuthenticationType.OAUTH2);
    }

    @Test
    void OAuth2PrincipalInfoNotAuthenticatedThrowException() throws PrincipalAuthenticationException {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(false);

        //ACT
        assertThrows(PrincipalAuthenticationException.class, () -> new OAuth2PrincipalInfo(authToken));
    }

    @Test
    void OAuth2PrincipalInfoNullPrincipalThrowException() throws PrincipalAuthenticationException {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(null);

        //ACT
        assertThrows(PrincipalAuthenticationException.class, () -> new OAuth2PrincipalInfo(authToken));
    }

    @Test
    void OAuth2PrincipalInfoNoOidcIdTokenThrowException() throws PrincipalAuthenticationException {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(new DefaultOAuth2User(AuthorityUtils.createAuthorityList("USER"), Map.of("Key1", "value1"), "Key1"));

        //ACT
        assertThrows(PrincipalAuthenticationException.class, () -> new OAuth2PrincipalInfo(authToken));
    }

    @Test
    void OAuth2PrincipalInfoNoEmailThrowException() throws PrincipalAuthenticationException {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(getOidcUserWithoutEmail());

        //ACT
        assertThrows(PrincipalAuthenticationException.class, () -> new OAuth2PrincipalInfo(authToken));
    }

    @Test
    void OAuth2PrincipalInfoNominal() throws PrincipalAuthenticationException {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(getNominalOidcUser());

        //ACT
        OAuth2PrincipalInfo auth2PrincipalInfo = new OAuth2PrincipalInfo(authToken);

        //CHECK
        assertThat(auth2PrincipalInfo.getEmail()).isEqualTo("pierre.paul.oc@gmail.com");
        assertThat(auth2PrincipalInfo.getFirstName()).isEqualTo("Pierre");
        assertThat(auth2PrincipalInfo.getLastName()).isEqualTo("Paul");
    }

    @Test
    void OAuth2PrincipalInfoNominalFirstNameNull() throws PrincipalAuthenticationException {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(getNominalWithoutFirstName());

        //ACT
        OAuth2PrincipalInfo auth2PrincipalInfo = new OAuth2PrincipalInfo(authToken);

        //CHECK
        assertThat(auth2PrincipalInfo.getEmail()).isEqualTo("pierre.paul.oc@gmail.com");
        assertNull(auth2PrincipalInfo.getFirstName());
        assertThat(auth2PrincipalInfo.getLastName()).isEqualTo("Paul");
    }

    @Test
    void OAuth2PrincipalInfoNominalLastNameNull() throws PrincipalAuthenticationException {
        //PREPARE
        when(authToken.isAuthenticated()).thenReturn(true);
        when(authToken.getPrincipal()).thenReturn(getNominalWithoutLastName());

        //ACT
        OAuth2PrincipalInfo auth2PrincipalInfo = new OAuth2PrincipalInfo(authToken);

        //CHECK
        assertThat(auth2PrincipalInfo.getEmail()).isEqualTo("pierre.paul.oc@gmail.com");
        assertThat(auth2PrincipalInfo.getFirstName()).isEqualTo("Pierre");
        assertNull(auth2PrincipalInfo.getLastName());
    }

    private DefaultOidcUser getNominalOidcUser(){

        OidcIdToken oidcIdToken = new OidcIdToken(
                "eryery.eytryterfdgdfhVYGGbw-zerz-eztret-flLN-38387357-4373",
                Instant.now(),
                Instant.now().plusSeconds(30),
                oidcIdTokenNominalClaims);
        return new DefaultOidcUser(null, oidcIdToken);
    }

    private DefaultOidcUser getOidcUserWithoutEmail(){
        oidcIdTokenNominalClaims.remove("email");
        OidcIdToken oidcIdToken = new OidcIdToken(
                "eryery.eytryterfdgdfhVYGGbw-zerz-eztret-flLN-38387357-4373",
                Instant.now(),
                Instant.now().plusSeconds(30),
                oidcIdTokenNominalClaims);
        return new DefaultOidcUser(null, oidcIdToken);
    }

    private DefaultOidcUser getNominalWithoutFirstName(){
        oidcIdTokenNominalClaims.remove("given_name");
        OidcIdToken oidcIdToken = new OidcIdToken(
                "eryery.eytryterfdgdfhVYGGbw-zerz-eztret-flLN-38387357-4373",
                Instant.now(),
                Instant.now().plusSeconds(30),
                oidcIdTokenNominalClaims);
        return new DefaultOidcUser(null, oidcIdToken);
    }

    private DefaultOidcUser getNominalWithoutLastName(){
        oidcIdTokenNominalClaims.remove("family_name");
        OidcIdToken oidcIdToken = new OidcIdToken(
                "eryery.eytryterfdgdfhVYGGbw-zerz-eztret-flLN-38387357-4373",
                Instant.now(),
                Instant.now().plusSeconds(30),
                oidcIdTokenNominalClaims);
        return new DefaultOidcUser(null, oidcIdToken);
    }

}