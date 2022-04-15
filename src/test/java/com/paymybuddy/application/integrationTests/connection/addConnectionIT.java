package com.paymybuddy.application.integrationTests.connection;

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
import org.springframework.transaction.annotation.Transactional;
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
public class addConnectionIT {

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
   @Transactional
   public void addConnectionExistentNotAlreadyAddedOK() throws Exception {

       String johnBoydEmail = "john.boyd@gmail.com";
       String reginoldWalkerdEmail = "reginold.walker@gmail.com";

       //check that connection is not added yet
       User johnBoyd = userRepository.findByEmail(johnBoydEmail).get();
       User reginoldWalker = userRepository.findByEmail(reginoldWalkerdEmail).get();
       assertThat(johnBoyd.getConnections()).doesNotContain(reginoldWalker);

       //execute add connection
       this.mvc.perform(MockMvcRequestBuilders.post("/connection")
                        .with(Client.johnBoyd())
                       .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                       .header("Referer","/transfer")
                       .param("email","reginold.walker@gmail.com")
                       .with(csrf()))
               .andDo(print())
               .andExpect(flash().attribute("error",nullValue())) //no error happened
               .andExpect(redirectedUrl("/transfer"))
               .andExpect(status().isFound());

       //check connections
       johnBoyd = userRepository.findByEmail(johnBoydEmail).get();
       assertThat(johnBoyd.getConnections()).contains(reginoldWalker);
    }

    @Test
    @Transactional
    public void addConnectionExistenAlreadyAddedFails() throws Exception {

        String johnBoydEmail = "john.boyd@gmail.com";
        String jacobBoydEmail = "jacob.boyd@gmail.com";

        //check that connection is not added yet
        User johnBoyd = userRepository.findByEmail(johnBoydEmail).get();
        User jacobBoyd = userRepository.findByEmail(jacobBoydEmail).get();
        assertThat(johnBoyd.getConnections()).containsOnlyOnce(jacobBoyd);

        //execute add connection
        this.mvc.perform(MockMvcRequestBuilders.post("/connection")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/transfer")
                        .param("email","jacob.boyd@gmail.com")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(redirectedUrl("/transfer"))
                .andExpect(status().isFound());

        //check that connection is not added twice
        johnBoyd = userRepository.findByEmail(johnBoydEmail).get();
        assertThat(johnBoyd.getConnections()).containsOnlyOnce(jacobBoyd);
    }

    @Test
    public void addConnectionNonexistentConnectionFails() throws Exception {

        //execute add connection
        this.mvc.perform(MockMvcRequestBuilders.post("/connection")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/transfer")
                        .param("email","nonexistent@gmail.com")
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(redirectedUrl("/transfer"))
                .andExpect(status().isFound());
    }
}
