package com.paymybuddy.application.controller.principalInfo;

import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
/**
 * Implements PrincipalInfo for username/password authentication
 */
@Getter
public class UsernamePasswordPrincipalInfo implements PrincipalInfo {

    private final String email;

    public UsernamePasswordPrincipalInfo(Authentication authToken) throws PrincipalAuthenticationException {
        this.email = extractEmailFromToken(authToken);
    }

    @Override
    public AuthenticationType authenticationType() {
        return AuthenticationType.USERNAME_PASSWORD;
    }

    /**
     * Extract the email of the principal token
     * @return email, null if principal is not authenticated
     * @throws com.paymybuddy.application.exception.PrincipalAuthenticationException when email cannot be extracted from principal
     */
    private String extractEmailFromToken(Authentication authToken) throws PrincipalAuthenticationException {

        if(authToken.isAuthenticated()){
            String email;
            User user = (User)authToken.getPrincipal();
            email =  user.getUsername();
            if(email != null) {
                return email;
            }
        }
        throw new PrincipalAuthenticationException("Failed to extract email from token");
    }

    public String getFirstName(){
        return null;
    }
    public String getLastName(){
        return null;
    }
}
