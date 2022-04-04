package com.paymybuddy.application.controller.principalInfo;

/**
 * Represents principal information after authentication
 */
public interface PrincipalInfo {
    /**
     * Gets the type of authentication of the principal
     * @return type of authentication
     */
    AuthenticationType authenticationType();
    /**
     * Gets the email of the principal
     * @return email, may be null according to used authentication
     */
    String getEmail();
    /**
     * Gets the first name of the principal
     * @return first name, may be null according to used authentication
     */
    String getFirstName();
    /**
     * Gets the last name of the principal
     * @return last name, may be null according to used authentication
     */
    String getLastName();
    /**
     * Authentication type
     **/
    enum AuthenticationType{
        USERNAME_PASSWORD,
        OAUTH2
    }
}
