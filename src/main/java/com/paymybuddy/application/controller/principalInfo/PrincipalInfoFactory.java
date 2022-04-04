package com.paymybuddy.application.controller.principalInfo;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.security.Principal;
/**
 * Factory that provides a PrincipalInfo implementation instance
 * according to principal Authentication Token type
 * */
public class PrincipalInfoFactory {
    /**
     * Get an PrincipalInfo implementation instance according to authentication type
     *
     * @return a PrincipalInfo object or null if authentication type is unknown
     * */
    public PrincipalInfo getPrincipalInfo(Principal principal)
    {
        if(principal instanceof UsernamePasswordAuthenticationToken){
            return new UsernamePasswordPrincipalInfo((UsernamePasswordAuthenticationToken)principal);
        }
        else if(principal instanceof OAuth2AuthenticationToken){
            return new OAuth2PrincipalInfo((OAuth2AuthenticationToken)principal);
        }
        else{
            return null;
        }
    }
}
