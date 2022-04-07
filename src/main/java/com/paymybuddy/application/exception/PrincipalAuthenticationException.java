package com.paymybuddy.application.exception;

/**
 * Exception to be thrown when principal is not authenticated, or
 * email of authenticated principal cannot be extracted from token (principal cannot be identified), or
 * authenticated principal is not registered into application database.
 */
public class PrincipalAuthenticationException extends Exception{
    public PrincipalAuthenticationException(String description){
        super(description);
    }
}
