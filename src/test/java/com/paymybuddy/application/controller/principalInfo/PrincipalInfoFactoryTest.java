package com.paymybuddy.application.controller.principalInfo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class PrincipalInfoFactoryTest {


    private PrincipalInfoFactory principalInfoFactory;

    @BeforeEach
    void initializeTest(){
        principalInfoFactory = new PrincipalInfoFactory();
    }


    @Test
    void getPrincipalInfoUserPasswordAuthentication() {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(null,null);
        PrincipalInfo principalInfo = principalInfoFactory.getPrincipalInfo(token);

        assertInstanceOf(UsernamePasswordPrincipalInfo.class, principalInfo);
    }

    @Test
    void getPrincipalOAuth2Authentication() {

        OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(getOAuth2User(),null,"id");
        PrincipalInfo principalInfo = principalInfoFactory.getPrincipalInfo(token);

        assertInstanceOf(OAuth2PrincipalInfo.class, principalInfo);
    }

    @Test
    void getPrincipalUnknownAuthentication() {

        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken("key","Pierre Paul");
        PrincipalInfo principalInfo = principalInfoFactory.getPrincipalInfo(token);

        assertNull(principalInfo) ;
    }

    private DefaultOidcUser getOAuth2User(){
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
        return new DefaultOidcUser(null, oidcIdToken);
    }
}