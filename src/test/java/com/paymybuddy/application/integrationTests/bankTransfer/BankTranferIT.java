package com.paymybuddy.application.integrationTests.bankTransfer;

import com.paymybuddy.application.integrationTests.util.Client;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.repository.UserRepository;
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

import javax.persistence.EntityManager;

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
public class BankTranferIT {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
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
   public void userBankTransferCreditMyBuddyAccount() throws Exception {

       //check balance of john before crediting his balance
       assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(1200);

       //execute bank transfer to credit mybuddy account
       this.mvc.perform(MockMvcRequestBuilders.post("/bankTransfer")
                        .with(Client.johnBoyd())
                       .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                       .header("Referer","/profile")
                       .param("transferType","CREDIT_MYBUDDY_ACCOUNT")
                       .param("bankId","1")
                       .param("amount", "54.45")
                       .with(csrf()))
               .andDo(print())
               .andExpect(flash().attribute("error",nullValue())) //no error happened
               .andExpect(redirectedUrl("/profile"))
               .andExpect(status().isFound());

       //check balance of john after crediting his balance
       assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(6645);
    }

    @Test
    public void userBankTransferCreditMaxPossibleValue() throws Exception {

        //check balance of john before crediting his balance
        assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(1200);

        //execute bank transfer to credit mybuddy account
        this.mvc.perform(MockMvcRequestBuilders.post("/bankTransfer")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/profile")
                        .param("transferType","CREDIT_MYBUDDY_ACCOUNT")
                        .param("bankId","1")
                        .param("amount", "999999999999999.99")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attribute("error",nullValue())) //no error happened
                .andExpect(redirectedUrl("/profile"))
                .andExpect(status().isFound());

        //check balance of john after crediting his balance
        assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(100000000000001199L);
    }

    @Test
    public void userBankTransferAmountOutOfRangeFails() throws Exception {

        //check balance of john before crediting his balance
        assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(1200);

        //execute bank transfer to credit mybuddy account
        this.mvc.perform(MockMvcRequestBuilders.post("/bankTransfer")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/profile")
                        .param("transferType","CREDIT_MYBUDDY_ACCOUNT")
                        .param("bankId","1")
                        .param("amount", "1000000000000000.00")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) // error happened
                .andExpect(redirectedUrl("/profile"))
                .andExpect(status().isFound());

        //check balance of john after crediting his balance
        assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(1200);
    }

    @Test
    public void userBankTransferBalanceOutOfRangeFails() throws Exception {

        //set balance of john to max value before crediting his balance
        User user = userRepository.findById(1).get();
        user.setBalance(Long.MAX_VALUE);
        userRepository.save(user);

        //execute bank transfer to credit mybuddy account
        this.mvc.perform(MockMvcRequestBuilders.post("/bankTransfer")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/profile")
                        .param("transferType","CREDIT_MYBUDDY_ACCOUNT")
                        .param("bankId","1")
                        .param("amount", "0.01")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) // error happened
                .andExpect(redirectedUrl("/profile"))
                .andExpect(status().isFound());

        //check balance of john after crediting his balance
        assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(Long.MAX_VALUE);
    }


    @Test
    public void userBankTransferBankIdOfOtherUserFails() throws Exception {

        //check balances of john and jacob before crediting his balance
        assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(1200);
        assertThat(userRepository.findById(2).get().getBalance()).isEqualTo(500000);

        //jacob try to execute bank transfer with john bank  to credit mybuddy account
        this.mvc.perform(MockMvcRequestBuilders.post("/bankTransfer")
                        .with(Client.jacobBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/profile")
                        .param("transferType","CREDIT_MYBUDDY_ACCOUNT")
                        .param("bankId","1")
                        .param("amount", "100")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) // error happened
                .andExpect(redirectedUrl("/profile"))
                .andExpect(status().isFound());

        //check balances of john and jacob after
        assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(1200);
        assertThat(userRepository.findById(2).get().getBalance()).isEqualTo(500000);
    }

    @Test
    public void userBankTransferDebitMyBuddyAccount() throws Exception {

        //check balance of john before crediting his balance
        assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(1200);

        //execute bank transfer to credit mybuddy account
        this.mvc.perform(MockMvcRequestBuilders.post("/bankTransfer")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/profile")
                        .param("transferType","DEBIT_MYBUDDY_ACCOUNT")
                        .param("bankId","1")
                        .param("amount", "9.84")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attribute("error",nullValue())) //no error happened
                .andExpect(redirectedUrl("/profile"))
                .andExpect(status().isFound());

        //check balance of john before crediting his balance
        assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(216);
    }

    @Test
    public void userBankTransferDebitAllBalance() throws Exception {

        //check balance of john before crediting his balance
        assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(1200);

        //execute bank transfer to credit mybuddy account
        this.mvc.perform(MockMvcRequestBuilders.post("/bankTransfer")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/profile")
                        .param("transferType","DEBIT_MYBUDDY_ACCOUNT")
                        .param("bankId","1")
                        .param("amount", "12.00")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attribute("error",nullValue())) //no error happened
                .andExpect(redirectedUrl("/profile"))
                .andExpect(status().isFound());

        //check balance of john after
        assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(0);
    }

    @Test
    public void userBankTransferDebitMoreThanBalance() throws Exception {

        //check balance of john before crediting his balance
        assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(1200);

        //execute bank transfer to credit mybuddy account
        this.mvc.perform(MockMvcRequestBuilders.post("/bankTransfer")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/profile")
                        .param("transferType","DEBIT_MYBUDDY_ACCOUNT")
                        .param("bankId","1")
                        .param("amount", "12.01")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) // error happened
                .andExpect(redirectedUrl("/profile"))
                .andExpect(status().isFound());

        //check balance of john after
        assertThat(userRepository.findById(1).get().getBalance()).isEqualTo(1200);
    }
}
