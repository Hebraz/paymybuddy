package com.paymybuddy.application.integrationTests.login;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/schema.sql", "/data.sql"}) //RELOAD database before each test
public class LoginLogoutIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void shouldReturnDefaultMessage() throws Exception {
        mvc.perform(get("/login")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void userLoginTestAuthenticated() throws Exception {
        mvc.perform(formLogin("/login").user("john.boyd@gmail.com").password("john.boyd")).andExpect(authenticated());
    }

    @Test
    public void userLogout() throws Exception {
        mvc.perform(formLogin("/login").user("john.boyd@gmail.com").password("john.boyd"));
        mvc.perform(logout()).andExpect(unauthenticated());
    }

    @Test
    public void userLoginTestUnauthenticatedWrongPwd() throws Exception {
        mvc.perform(formLogin("/login").user("john.boyd@gmail.com").password("john.boydy")).andExpect(unauthenticated());
    }
}
