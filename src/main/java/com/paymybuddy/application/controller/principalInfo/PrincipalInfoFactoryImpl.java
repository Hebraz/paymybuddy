package com.paymybuddy.application.controller.principalInfo;

import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * Implements a PrincipalInfoFactory that provides a PrincipalInfo implementation instance
 * according to principal Authentication Token type
 * */
@Component
public class PrincipalInfoFactoryImpl implements PrincipalInfoFactory {
    /**
     * Get an PrincipalInfo implementation instance according to authentication type
     *
     * @return a PrincipalInfo object
     * @throws PrincipalAuthenticationException if authentication token is not supported
     * */
    public PrincipalInfo getPrincipalInfo(Principal principal) throws PrincipalAuthenticationException {
        if(principal instanceof UsernamePasswordAuthenticationToken){
            return new UsernamePasswordPrincipalInfo((UsernamePasswordAuthenticationToken)principal);
        }
        else if(principal instanceof OAuth2AuthenticationToken){
            return new OAuth2PrincipalInfo((OAuth2AuthenticationToken)principal);
        }
        else{
            throw new PrincipalAuthenticationException("Authentication token not supported: " + principal.getClass().getSimpleName());
        }
    }
}
