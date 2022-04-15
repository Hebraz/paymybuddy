package com.paymybuddy.application.integrationTests.bankAccount;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.application.integrationTests.util.Client;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.repository.BankAccountRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/schema.sql", "/data.sql"}) //RELOAD database before each test
public class UpdateBankAccountIT {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }



   @Test
   public void updateBankAccountUserAuthenticated() throws Exception {

       String bankDescription = "XXXXXXXXXXXXXXXXXXXXXXXXX"; //25 characters
       String bankIban = "FR77777777777777777777777";
       int bankAccountId = 2;
       BankAccount bankAccount ;

       //CHECK BEFORE
       bankAccount = bankAccountRepository.findById(bankAccountId).get();
       assertThat(bankAccount.getIban()).isEqualTo("FR01234567890123456741852");
       assertThat(bankAccount.getDescription()).isEqualTo("BNP");

       //update bank account
       this.mvc.perform(MockMvcRequestBuilders.put("/bankAccount")
                        .with(Client.johnBoyd())
                       .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                       .param("id", String.valueOf(bankAccountId))
                       .param("iban",bankIban)
                       .param("description",bankDescription)
                       .with(csrf()))
               .andDo(print())
               .andExpect(flash().attribute("error",nullValue())) //no error happened
               .andExpect(status().isFound());

       //CHECK AFTER
       bankAccount = bankAccountRepository.findById(bankAccountId).get();
       assertThat(bankAccount.getIban()).isEqualTo("FR77777777777777777777777");
       assertThat(bankAccount.getDescription()).isEqualTo("XXXXXXXXXXXXXXXXXXXXXXXXX");
   }

    @Test
    public void updateBankAccountOfAnotherUser() throws Exception {

        String bankDescription = "Bank of America";
        String bankIban = "FR77777777777777777777777";
        int bankAccountId = 2;

        //CHECK bank account id 2, john.boyd one's
        assertThat(bankAccountRepository.findById(bankAccountId).get().getIban()).isEqualTo("FR01234567890123456741852");

        //try to update bank account with jaccob account
        this.mvc.perform(MockMvcRequestBuilders.put("/bankAccount")
                        .with(Client.jacobBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("id", String.valueOf(bankAccountId))
                        .param("iban",bankIban)
                        .param("description",bankDescription)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(status().isFound());

        //CHECK bank account id 2, john.boyd one's => not modified
        assertThat(bankAccountRepository.findById(bankAccountId).get().getIban()).isEqualTo("FR01234567890123456741852");

    }

    @Test
    public void updateBankAccountUserUnauthenticatedRedirectLogin() throws Exception {

        String bankDescription = "Bank of America";
        String bankIban = "FR77777777777777777777777";
        int bankAccountId = 2;

        //update bank account
        this.mvc.perform(MockMvcRequestBuilders.put("/bankAccount")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("id", String.valueOf(bankAccountId))
                        .param("iban",bankIban)
                        .param("description",bankDescription)
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("http://localhost/login"))
                .andExpect(status().isFound());
    }

    @Test
    public void updateBankAccountIbanNotValidFails() throws Exception {

        String bankDescription = "Bank of America";
        String bankIban = "FA77777777777777777777777"; //does not begin with FR
        int bankAccountId = 2;

        //CHECK BEFORE
        assertThat(bankAccountRepository.findById(bankAccountId).get().getIban()).isEqualTo("FR01234567890123456741852");

        //update bank account
        this.mvc.perform(MockMvcRequestBuilders.put("/bankAccount")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("id", String.valueOf(bankAccountId))
                        .param("iban",bankIban)
                        .param("description",bankDescription)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(status().isFound());

        //CHECK AFTER, IBAN not modified
        assertThat(bankAccountRepository.findById(bankAccountId).get().getIban()).isEqualTo("FR01234567890123456741852");
    }

    @Test
    public void updateBankAccountDescriptionNotValidFails() throws Exception {

        String bankDescription = "XXXXXXXXXXXXXXXXXXXXXXXXXX"; //26 characteres instead of 25 max
        String bankIban = "FR77777777777777777777777";
        int bankAccountId = 2;
        BankAccount bankAccount;

        //CHECK BEFORE
        bankAccount = bankAccountRepository.findById(bankAccountId).get();
        assertThat(bankAccount.getIban()).isEqualTo("FR01234567890123456741852");
        assertThat(bankAccount.getDescription()).isEqualTo("BNP");

        //update bank account
        this.mvc.perform(MockMvcRequestBuilders.put("/bankAccount")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("id", String.valueOf(bankAccountId))
                        .param("iban",bankIban)
                        .param("description",bankDescription)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(status().isFound());

        //CHECK AFTER, IBAN not modified
        bankAccount = bankAccountRepository.findById(bankAccountId).get();
        assertThat(bankAccount.getIban()).isEqualTo("FR01234567890123456741852");
        assertThat(bankAccount.getDescription()).isEqualTo("BNP");
    }

    @Test
    public void updateBankAccountIdDoesNotExistFails() throws Exception {

        String bankDescription = "XXXXXXXXXXXXXXXXXXXXXXXXX";
        String bankIban = "FR77777777777777777777777";
        int bankAccountId = 35;
        Optional<BankAccount> bankAccount;

        //update bank account
        this.mvc.perform(MockMvcRequestBuilders.put("/bankAccount")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("id", String.valueOf(bankAccountId))
                        .param("iban",bankIban)
                        .param("description",bankDescription)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(status().isFound());

        //CHECK AFTER, IBAN not created
        bankAccount = bankAccountRepository.findById(bankAccountId);
        assertTrue(bankAccount.isEmpty());
    }
}
