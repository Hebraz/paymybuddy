package com.paymybuddy.application.exception;

public class ForbiddenOperationException extends Exception {
    public ForbiddenOperationException(String description){
        super(description);
    }
}
