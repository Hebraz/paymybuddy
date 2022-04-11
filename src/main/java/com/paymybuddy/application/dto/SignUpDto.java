package com.paymybuddy.application.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SignUpDto {
    @Email
    @Size(max=100, message = "Email must be at most 100 characters in length")
    private String email;

    @Email
    @Size(max=100, message = "Email must be at most 100 characters in length")
    private String emailConfirmation;

    @Size(min=8, max=100, message = "Password must be between 8 and 100 characters in length")
    private String password;

    @Size(min=1, max=50, message = "First name must be between 1 and 50 characters in length")
    private String firstName;

    @Size(min=1, max=50, message = "Last name must be between 1 and 50 characters in length")
    private String lastName;


    public SignUpDto(String email, String emailConfirmation, String password, String firstName, String lastName) {
        this.email = email;
        this.emailConfirmation = emailConfirmation;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public SignUpDto() {
    }
}
