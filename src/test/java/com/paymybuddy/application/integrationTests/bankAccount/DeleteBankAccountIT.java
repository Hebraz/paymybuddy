package com.paymybuddy.application.integrationTests.bankAccount;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.application.integrationTests.util.Client;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/schema.sql", "/data.sql"}) //RELOAD database before each test
public class DeleteBankAccountIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

   @Test
   public void userDeleteAccountOfAuthenticatedUser() throws Exception {
       String content;
       MvcResult result;
       int bankId = 1;
       String bankId1Iban = "FR01234567890123456789012";

       //check that bank account appears in profile view before deleting it
       result = this.mvc.perform(MockMvcRequestBuilders.get("/profile")
                       .with(Client.johnBoyd())
                       .with(csrf()))
               .andReturn();
       content = result.getResponse().getContentAsString();
       assertThat(content).contains(bankId1Iban);

       //delete bank account
       this.mvc.perform(MockMvcRequestBuilders.delete("/bankAccount/"+ bankId)
                        .with(Client.johnBoyd())
                       .header("Referer","/profile")
                       .with(csrf()))
               .andDo(print())
               .andExpect(flash().attribute("error",nullValue())) //no error happened
               .andExpect(redirectedUrl("/profile"))
               .andExpect(status().isFound());

       //check that bank account does not appear anymore in profile view
       result = this.mvc.perform(MockMvcRequestBuilders.get("/profile")
                   .with(Client.johnBoyd())
                   .with(csrf()))
               .andReturn();
       content = result.getResponse().getContentAsString();
       assertThat(content).doesNotContain(bankId1Iban);
    }

    @Test
    public void userDeleteAccountOfUnauthenticatedUserRedirectLogin() throws Exception {
        int bankId = 1;

        //delete bank account
        this.mvc.perform(MockMvcRequestBuilders.delete("/bankAccount/"+ bankId)
                        .header("Referer","/profile")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("http://localhost/login"))
                .andExpect(status().isFound());
    }

    @Test
    public void userDeleteAccountOfAnotherUser() throws Exception {
        String content;
        MvcResult result;
        int bankId = 5;
        String bankId5Iban = "FR01123156418312894733333";

        //check that bank account appears in profile view of jacob before deleting it
        result = this.mvc.perform(MockMvcRequestBuilders.get("/profile")
                        .with(Client.jacobBoyd())
                        .with(csrf()))
                .andReturn();
        content = result.getResponse().getContentAsString();
        assertThat(content).contains(bankId5Iban);

        //delete bank account from john account
        this.mvc.perform(MockMvcRequestBuilders.delete("/bankAccount/"+ bankId)
                        .with(Client.johnBoyd())
                        .header("Referer","/profile")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(redirectedUrl("/profile"))
                .andExpect(status().isFound());

        //check that bank account always appears in profile view of jacob before deleting it
        result = this.mvc.perform(MockMvcRequestBuilders.get("/profile")
                        .with(Client.jacobBoyd())
                        .with(csrf()))
                .andReturn();
        content = result.getResponse().getContentAsString();
        assertThat(content).contains(bankId5Iban);
    }
}
