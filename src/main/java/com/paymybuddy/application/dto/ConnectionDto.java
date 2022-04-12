package com.paymybuddy.application.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class ConnectionDto {
    @Email
    private String email;

    public ConnectionDto(String email) {
        this.email = email;
    }

    public ConnectionDto(){}
}
