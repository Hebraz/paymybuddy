package com.paymybuddy.application.integrationTests.bankAccount;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.application.integrationTests.util.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/schema.sql", "/data.sql"}) //RELOAD database before each test
public class AddBankAccountIT {

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
   public void userAddAccountUserAuthenticated() throws Exception {

       String bankDescription = "Bank of America";
       String bankIban = "FR88845678912377678917854";

       //Add bank account
       this.mvc.perform(MockMvcRequestBuilders.post("/bankAccount")
                        .with(Client.johnBoyd())
                       .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                       .header("Referer","/profile")
                       .param("iban",bankIban)
                       .param("description",bankDescription)
                       .with(csrf()))
               .andDo(print())
               .andExpect(flash().attribute("error",nullValue())) //no error happened
               .andExpect(redirectedUrl("/profile"))
               .andExpect(status().isFound());

       //check that bank account appears in profile view
       MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/profile")
                   .with(Client.johnBoyd())
                   .with(csrf()))
               .andReturn();

       String content = result.getResponse().getContentAsString();
       assertThat(content).contains(bankIban);
       assertThat(content).contains(bankDescription);

    }

    @Test
    public void userAddAccountAlreadyExistsFails() throws Exception {

        String bankDescription = "Bank that already exist";
        String alreadyUsedIban = "FR01234567890123456789012";

        //try to add bank account
        this.mvc.perform(MockMvcRequestBuilders.post("/bankAccount")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/profile")
                        .param("iban",alreadyUsedIban)
                        .param("description",bankDescription)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error appears
                .andExpect(redirectedUrl("/profile"))
                .andExpect(status().isFound());

        //check that bank account does not appear in profile view
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/profile")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain(bankDescription);

    }

    @Test
    public void userAddAccountNotValidIbanFails() throws Exception {

        String bankDescription = "Bank with bad iban";
        String bankIban = "FR8884567891237767891785"; //22 digits instead of 23

        //try to add bank account
        this.mvc.perform(MockMvcRequestBuilders.post("/bankAccount")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/profile")
                        .param("iban",bankIban)
                        .param("description",bankDescription)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error appears
                .andExpect(redirectedUrl("/profile"))
                .andExpect(status().isFound());

        //check that bank account does not appear in profile view
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/profile")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain(bankDescription);
    }

    @Test
    public void userAddAccountNotValidDescriptionFails() throws Exception {

        String badBankDescription = "B"; //at least 2 digits needed
        String bankIban = "FR88845678912377627891785";

        //try to add bank account
        this.mvc.perform(MockMvcRequestBuilders.post("/bankAccount")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/profile")
                        .param("iban",bankIban)
                        .param("description",badBankDescription)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error appears
                .andExpect(redirectedUrl("/profile"))
                .andExpect(status().isFound());

        //check that bank account does not appear in profile view
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/profile")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain(bankIban);
    }

    @Test
    public void userAddAccountUserUnauthenticatedRedirectToLogin() throws Exception {

        //Add bank account
        this.mvc.perform(MockMvcRequestBuilders.post("/bankAccount")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer", "/profile")
                        .param("iban", "FR74145678912377678917854")
                        .param("description", "My Bank")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("http://localhost/login")) //redirection to login
                .andExpect(status().isFound());
    }
}
