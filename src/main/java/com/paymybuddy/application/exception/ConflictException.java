package com.paymybuddy.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when trying to create a resource that already exist
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends Exception {
    ConflictException(String description){
        super(description);
    }
}