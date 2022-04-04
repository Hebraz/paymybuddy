package com.paymybuddy.application.controller.principalInfo;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
/**
 * Implements PrincipalInfo for username/password authentication
 */
public class UsernamePasswordPrincipalInfo implements PrincipalInfo {
    private final Authentication authToken;

    public UsernamePasswordPrincipalInfo(Authentication authToken) {
        this.authToken = authToken;
    }

    @Override
    public AuthenticationType authenticationType() {
        return AuthenticationType.USERNAME_PASSWORD;
    }

    /**
     * Gets the email of the principal
     * @return email, null if principal is not authenticated
     */
    @Override
    public String getEmail() {
        if(authToken.isAuthenticated()){
            User user = (User)authToken.getPrincipal();
            return user.getUsername();
        }
        return null;
    }
    /**
     * Gets the first name of the principal
     * @return always null, this type of authentication does not provide first name
     */
    @Override
    public String getFirstName() {
        return null;
    }
    /**
     * Gets the last name of the principal
     * @return always null, this type of authentication does not provide last name
     */
    @Override
    public String getLastName() {
        return null;
    }
}
