package com.paymybuddy.application.integrationTests.transaction;

import com.paymybuddy.application.integrationTests.util.Client;
import com.paymybuddy.application.model.Transaction;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.repository.TransactionRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.time.Instant;

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
public class transactionIT {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
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
   @Transactional
   public void transactionNominal() throws Exception {

        String johnEmail = "john.boyd@gmail.com";
        String tessaEmail = "tessa.carman@gmail.com";
        int johnId = 1;
        int tessaId = 7;

       //check balance of john and tessa before transaction
       assertThat(userRepository.findByEmail(johnEmail).get().getBalance()).isEqualTo(1200);
       assertThat(userRepository.findByEmail(tessaEmail).get().getBalance()).isEqualTo(99999999999999L);

       //execute transaction from john to tessa
       this.mvc.perform(MockMvcRequestBuilders.post("/transfer")
                        .with(Client.johnBoyd())
                       .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                       .header("Referer","/transfer")
                       .param("connectionEmail",tessaEmail)
                       .param("amount","10.53")
                       .param("description", "Cinema tickets")
                       .with(csrf()))
               .andDo(print())
               .andExpect(flash().attribute("error",nullValue())) //no error happened
               .andExpect(flash().attributeExists("success")) //no error happened
               .andExpect(redirectedUrl("/transfer"))
               .andExpect(status().isFound());

       //check balance of john and tessa after transaction
       assertThat(userRepository.findByEmail(johnEmail).get().getBalance()).isEqualTo(147);
       assertThat(userRepository.findByEmail(tessaEmail).get().getBalance()).isEqualTo(100000000001052L);
       //check that transaction is recorded
       long nbTransaction = transactionRepository.count();
       Transaction lastTransaction = transactionRepository.findById((int)nbTransaction).get();
       assertThat(lastTransaction)
               .extracting(Transaction::getTotalAmount,
                            Transaction::getFeeAmount,
                            t -> t.getCredit().getId(),
                            t -> t.getPayer().getId(),
                            Transaction::getDescription)
               .containsExactly(1053L, 52L, tessaId, johnId,  "Cinema tickets");

       assertThat(lastTransaction.getDate()).isBetween(Instant.now().minusSeconds(2), Instant.now());
    }

    @Test
    public void transactionBalanceLessThanAmountFails() throws Exception {

        String johnEmail = "john.boyd@gmail.com";
        String tessaEmail = "tessa.carman@gmail.com";

        //check balance of john and tessa before transaction
        assertThat(userRepository.findByEmail(johnEmail).get().getBalance()).isEqualTo(1200);
        assertThat(userRepository.findByEmail(tessaEmail).get().getBalance()).isEqualTo(99999999999999L);
        long nbTransactionBefore =  transactionRepository.count();

        //execute transaction from john to tessa
        this.mvc.perform(MockMvcRequestBuilders.post("/transfer")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/transfer")
                        .param("connectionEmail",tessaEmail)
                        .param("amount","12.01")
                        .param("description", "Cinema tickets")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attribute("success",nullValue()))
                .andExpect(flash().attributeExists("error"))
                .andExpect(redirectedUrl("/transfer"))
                .andExpect(status().isFound());

        //check balance of john and tessa after transaction
        assertThat(userRepository.findByEmail(johnEmail).get().getBalance()).isEqualTo(1200);
        assertThat(userRepository.findByEmail(tessaEmail).get().getBalance()).isEqualTo(99999999999999L);
        assertThat(transactionRepository.count()).isEqualTo(nbTransactionBefore);
    }


    @Test
    @Transactional
    public void transactionAmountMax() throws Exception {

        String johnEmail = "john.boyd@gmail.com";
        String tessaEmail = "tessa.carman@gmail.com";
        int johnId = 1;
        int tessaId = 7;
        //set tessa balance to 100000000000000
        User tessa = userRepository.findByEmail(tessaEmail).get();
        tessa.setBalance(100000000000000000L);
        userRepository.save(tessa);

        //check balance of john and tessa before transaction
        assertThat(userRepository.findByEmail(johnEmail).get().getBalance()).isEqualTo(1200);
        assertThat(userRepository.findByEmail(tessaEmail).get().getBalance()).isEqualTo(100000000000000000L);

        //execute transaction from tessa to john
        this.mvc.perform(MockMvcRequestBuilders.post("/transfer")
                        .with(Client.tessaCarman())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/transfer")
                        .param("connectionEmail",johnEmail)
                        .param("amount","999999999999999.99")
                        .param("description", "Ferrari")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attribute("error",nullValue()))
                .andExpect(flash().attributeExists("success"))
                .andExpect(redirectedUrl("/transfer"))
                .andExpect(status().isFound());

        //check balance of john and tessa after transaction
        assertThat(userRepository.findByEmail(johnEmail).get().getBalance()).isEqualTo(100000000000001199L);
        assertThat(userRepository.findByEmail(tessaEmail).get().getBalance()).isEqualTo(1L);
        //check that transaction is recorded
        long nbTransaction = transactionRepository.count();
        Transaction lastTransaction = transactionRepository.findById((int)nbTransaction).get();
        assertThat(lastTransaction)
                .extracting(Transaction::getTotalAmount,
                        Transaction::getFeeAmount,
                        t -> t.getCredit().getId(),
                        t -> t.getPayer().getId(),
                        Transaction::getDescription)
                .containsExactly(99999999999999999L, 4999999999999999L, johnId, tessaId,  "Ferrari");

        assertThat(lastTransaction.getDate()).isBetween(Instant.now().minusSeconds(2), Instant.now());
    }

    @Test
    public void transactionAmountUpperThanMaxPossibleValueFails() throws Exception {

        String johnEmail = "john.boyd@gmail.com";
        String tessaEmail = "tessa.carman@gmail.com";
        //set tessa balance to 100000000000000
        User tessa = userRepository.findByEmail(tessaEmail).get();
        tessa.setBalance(100000000000000000L);
        userRepository.save(tessa);

        //check balance of john and tessa before transaction
        assertThat(userRepository.findByEmail(johnEmail).get().getBalance()).isEqualTo(1200);
        assertThat(userRepository.findByEmail(tessaEmail).get().getBalance()).isEqualTo(100000000000000000L);
        long nbTransactionBefore =  transactionRepository.count();

        //execute transaction from tessa to john
        this.mvc.perform(MockMvcRequestBuilders.post("/transfer")
                        .with(Client.tessaCarman())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/transfer")
                        .param("connectionEmail",johnEmail)
                        .param("amount","1000000000000000")
                        .param("description", "Ferrari")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attribute("success",nullValue()))
                .andExpect(flash().attributeExists("error"))
                .andExpect(redirectedUrl("/transfer"))
                .andExpect(status().isFound());

        //check balance of john and tessa after transaction => no change, no transaction
        assertThat(userRepository.findByEmail(johnEmail).get().getBalance()).isEqualTo(1200);
        assertThat(userRepository.findByEmail(tessaEmail).get().getBalance()).isEqualTo(100000000000000000L);
        assertThat(transactionRepository.count()).isEqualTo(nbTransactionBefore);
    }

    @Test
    public void transactionAmountLessThanMinPossibleValueFails() throws Exception {

        String johnEmail = "john.boyd@gmail.com";
        String tessaEmail = "tessa.carman@gmail.com";

        //set tessa balance to 100000000000000
        User tessa = userRepository.findByEmail(tessaEmail).get();
        tessa.setBalance(100000000000000000L);
        userRepository.save(tessa);

        //check balance of john and tessa before transaction
        assertThat(userRepository.findByEmail(johnEmail).get().getBalance()).isEqualTo(1200);
        assertThat(userRepository.findByEmail(tessaEmail).get().getBalance()).isEqualTo(100000000000000000L);
        long nbTransactionBefore =  transactionRepository.count();

        //execute transaction from tessa to john
        this.mvc.perform(MockMvcRequestBuilders.post("/transfer")
                        .with(Client.tessaCarman())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/transfer")
                        .param("connectionEmail",johnEmail)
                        .param("amount","0.009")
                        .param("description", "Ferrari")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attribute("success",nullValue()))
                .andExpect(flash().attributeExists("error"))
                .andExpect(redirectedUrl("/transfer"))
                .andExpect(status().isFound());

        //check balance of john and tessa after transaction => no change, no transaction
        assertThat(userRepository.findByEmail(johnEmail).get().getBalance()).isEqualTo(1200);
        assertThat(userRepository.findByEmail(tessaEmail).get().getBalance()).isEqualTo(100000000000000000L);
        assertThat(transactionRepository.count()).isEqualTo(nbTransactionBefore);
    }

    @Test
    public void transactionAmountCreditBalanceFullFails() throws Exception {

        String johnEmail = "john.boyd@gmail.com";
        String tessaEmail = "tessa.carman@gmail.com";
        //set tessa balance to 100000000000000
        User tessa = userRepository.findByEmail(tessaEmail).get();
        tessa.setBalance(Long.MAX_VALUE);
        userRepository.save(tessa);

        //check balance of john and tessa before transaction
        assertThat(userRepository.findByEmail(johnEmail).get().getBalance()).isEqualTo(1200);
        assertThat(userRepository.findByEmail(tessaEmail).get().getBalance()).isEqualTo(Long.MAX_VALUE);
        long nbTransactionBefore =  transactionRepository.count();

        //execute transaction from john to tessa
        this.mvc.perform(MockMvcRequestBuilders.post("/transfer")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/transfer")
                        .param("connectionEmail",tessaEmail)
                        .param("amount","0.01")
                        .param("description", "Ferrari")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attribute("success",nullValue()))
                .andExpect(flash().attributeExists("error"))
                .andExpect(redirectedUrl("/transfer"))
                .andExpect(status().isFound());



        //check balance of john and tessa after transaction => no change, no transaction
        assertThat(transactionRepository.count()).isEqualTo(nbTransactionBefore);
        assertThat( userRepository.findByEmail(johnEmail).get().getBalance()).isEqualTo(1200);
        assertThat(userRepository.findByEmail(tessaEmail).get().getBalance()).isEqualTo(Long.MAX_VALUE);

    }



}
