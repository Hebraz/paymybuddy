package com.paymybuddy.application.controller.principalInfo;

import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
/**
 * Factory that provides a PrincipalInfo implementation instance
 * according to principal Authentication Token type
 * */
@Component
public interface PrincipalInfoFactory {
    /**
     * Get an PrincipalInfo implementation instance according to authentication type
     *
     * @return a PrincipalInfo object
     * @throws PrincipalAuthenticationException if authentication token is not supported
     * */
     PrincipalInfo getPrincipalInfo(Principal principal) throws PrincipalAuthenticationException;
}
