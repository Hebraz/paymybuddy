package com.paymybuddy.application.integrationTests.util;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class Client {
    public static RequestPostProcessor johnBoyd(){
        return user("john.boyd@gmail.com").password("john.boyd");
    }
    public static RequestPostProcessor jacobBoyd(){
        return user("jacob.boyd@gmail.com").password("jacob.boyd");
    }
    public static RequestPostProcessor tessaCarman(){
        return user("tessa.carman@gmail.com").password("tessa.carman");
    }

    public static RequestPostProcessor unauthenticated(){
        return user("nobody").password("0000");
    }
}
