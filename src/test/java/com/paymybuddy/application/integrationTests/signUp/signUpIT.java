package com.paymybuddy.application.integrationTests.signUp;

import com.paymybuddy.application.model.User;
import com.paymybuddy.application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;

import javax.persistence.EntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/schema.sql", "/data.sql"}) //RELOAD database before each test
public class signUpIT {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    EntityManager em;
    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

   @Test
   public void signupNewUserOK() throws Exception {

       String userEmail = "pierre.paul.oc@gmail.com";
       String password = "pierre.paul.oc";
       String firstName = "Pierre";
       String lastName = "Paul";

       //check that new user account does not exist yet
       assertTrue(userRepository.findByEmail(userEmail).isEmpty());

       //execute signup
       this.mvc.perform(MockMvcRequestBuilders.post("/signUp")
                       .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                       .param("email",userEmail)
                       .param("emailConfirmation",userEmail)
                       .param("password",password)
                       .param("firstName",firstName)
                       .param("lastName",lastName)
                       .with(csrf()))
               .andDo(print())
               .andExpect(flash().attribute("error",nullValue())) //no error happened
               .andExpect(redirectedUrl("/home"))
               .andExpect(authenticated())
               .andExpect(status().isFound());

       //check that new user account does not exist yet
       Optional<User> userResult = userRepository.findByEmail(userEmail);
       assertTrue(userResult.isPresent());
       User user = userResult.get();
       assertThat(user)
               .extracting(
                       User::getEmail,
                       User::getFirstName,
                       User::getLastName,
                       User::getBalance)
               .containsExactly(userEmail, firstName, lastName, 0L);

       assertThat(user.getAuthority().getAuthority()).isEqualTo("ROLE_USER");
       BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
       assertTrue(encoder.matches(password,user.getPassword()));
    }

    @Test
    public void signupAlreadyExistentUserFails() throws Exception {

        String userEmail = "john.boyd@gmail.com";
        String password = "newpassword";
        String firstName = "J";
        String lastName = "B";

        //check that new user account exist
        assertTrue(userRepository.findByEmail(userEmail).isPresent());

        //try to execute signup with same email
        this.mvc.perform(MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/signUp")
                        .param("email",userEmail)
                        .param("emailConfirmation",userEmail)
                        .param("password",password)
                        .param("firstName",firstName)
                        .param("lastName",lastName)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(redirectedUrl("/signUp"))
                .andExpect(unauthenticated())
                .andExpect(status().isFound());

        //check that user account has not been updated
        Optional<User> userResult = userRepository.findByEmail(userEmail);
        assertTrue(userResult.isPresent());
        User user = userResult.get();
        assertThat(user)
                .extracting(
                        User::getEmail,
                        User::getFirstName,
                        User::getLastName,
                        User::getBalance)
                .containsExactly(userEmail, "John", "Boyd", 1200L);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("john.boyd",user.getPassword()));
    }

    @Test
    public void signupEmailsDifferentFails() throws Exception {

        String userEmail = "pierre.paul.oc@gmail.com";
        String userEmaiConfirmation = "pierr.paul.oc@gmail.com";
        String password = "pierre.paul.oc";
        String firstName = "Pierre";
        String lastName = "Paul";

        //check that new user account does not exist
        assertTrue(userRepository.findByEmail(userEmail).isEmpty());

        //try to execute signup with same email
        this.mvc.perform(MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/signUp")
                        .param("email",userEmail)
                        .param("emailConfirmation",userEmaiConfirmation)
                        .param("password",password)
                        .param("firstName",firstName)
                        .param("lastName",lastName)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(redirectedUrl("/signUp"))
                .andExpect(unauthenticated())
                .andExpect(status().isFound());

        //check that new user account does not exist
        assertTrue(userRepository.findByEmail(userEmail).isEmpty());
    }

    @Test
    public void signupNoFirstNameFails() throws Exception {

        String userEmail = "pierre.paul.oc@gmail.com";
        String password = "pierre.paul.oc";
        String firstName = "";
        String lastName = "Paul";

        //check that new user account does not exist
        assertTrue(userRepository.findByEmail(userEmail).isEmpty());

        //try to execute signup with same email
        this.mvc.perform(MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/signUp")
                        .param("email",userEmail)
                        .param("emailConfirmation",userEmail)
                        .param("password",password)
                        .param("firstName",firstName)
                        .param("lastName",lastName)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(redirectedUrl("/signUp"))
                .andExpect(unauthenticated())
                .andExpect(status().isFound());

        //check that new user account does not exist
        assertTrue(userRepository.findByEmail(userEmail).isEmpty());
    }

    @Test
    public void signupNoFirstNameTooLongFails() throws Exception {

        String userEmail = "pierre.paul.oc@gmail.com";
        String password = "pierre.paul.oc";
        String firstName = "azertyuiopazertyuiopazertyuiopazertyuiopazertyuiopa"; //51 characters instead of 50 max
        String lastName = "Paul";

        //check that new user account does not exist
        assertTrue(userRepository.findByEmail(userEmail).isEmpty());

        //try to execute signup with same email
        this.mvc.perform(MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/signUp")
                        .param("email",userEmail)
                        .param("emailConfirmation",userEmail)
                        .param("password",password)
                        .param("firstName",firstName)
                        .param("lastName",lastName)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(redirectedUrl("/signUp"))
                .andExpect(unauthenticated())
                .andExpect(status().isFound());

        //check that new user account does not exist
        assertTrue(userRepository.findByEmail(userEmail).isEmpty());
    }
    @Test
    public void signupNoLastNameFails() throws Exception {

        String userEmail = "pierre.paul.oc@gmail.com";
        String password = "pierre.paul.oc";
        String firstName = "Pierre";
        String lastName = "";

        //check that new user account does not exist
        assertTrue(userRepository.findByEmail(userEmail).isEmpty());

        //try to execute signup with same email
        this.mvc.perform(MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/signUp")
                        .param("email",userEmail)
                        .param("emailConfirmation",userEmail)
                        .param("password",password)
                        .param("firstName",firstName)
                        .param("lastName",lastName)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(redirectedUrl("/signUp"))
                .andExpect(unauthenticated())
                .andExpect(status().isFound());

        //check that new user account does not exist
        assertTrue(userRepository.findByEmail(userEmail).isEmpty());
    }

    @Test
    public void signupNoLastNameTooLongFails() throws Exception {

        String userEmail = "pierre.paul.oc@gmail.com";
        String password = "pierre.paul.oc";
        String firstName = "Pierre";
        String lastName =  "azertyuiopazertyuiopazertyuiopazertyuiopazertyuiopa"; //51 characters instead of 50 max

        //check that new user account does not exist
        assertTrue(userRepository.findByEmail(userEmail).isEmpty());

        //try to execute signup with same email
        this.mvc.perform(MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/signUp")
                        .param("email",userEmail)
                        .param("emailConfirmation",userEmail)
                        .param("password",password)
                        .param("firstName",firstName)
                        .param("lastName",lastName)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(redirectedUrl("/signUp"))
                .andExpect(unauthenticated())
                .andExpect(status().isFound());

        //check that new user account does not exist
        assertTrue(userRepository.findByEmail(userEmail).isEmpty());
    }

    @Test
    public void signupPasswordToShortFails() throws Exception {

        String userEmail = "pierre.paul.oc@gmail.com";
        String password = "1234567"; //7 characters instead of 8 min
        String firstName = "Pierre";
        String lastName =  "Paul";

        //check that new user account does not exist
        assertTrue(userRepository.findByEmail(userEmail).isEmpty());

        //try to execute signup with same email
        this.mvc.perform(MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/signUp")
                        .param("email",userEmail)
                        .param("emailConfirmation",userEmail)
                        .param("password",password)
                        .param("firstName",firstName)
                        .param("lastName",lastName)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(redirectedUrl("/signUp"))
                .andExpect(unauthenticated())
                .andExpect(status().isFound());

        //check that new user account does not exist
        assertTrue(userRepository.findByEmail(userEmail).isEmpty());
    }

    @Test
    public void signupPasswordToLongFails() throws Exception {

        String userEmail = "pierre.paul.oc@gmail.com";
        String password = "01234567890123456789012345678901234567890123456789" +
                "012345678901234567890123456789012345678901234567890"; //101 characters instead of 100 max
        String firstName = "Pierre";
        String lastName =  "Paul";

        //check that new user account does not exist
        assertTrue(userRepository.findByEmail(userEmail).isEmpty());

        //try to execute signup with same email
        this.mvc.perform(MockMvcRequestBuilders.post("/signUp")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("Referer","/signUp")
                        .param("email",userEmail)
                        .param("emailConfirmation",userEmail)
                        .param("password",password)
                        .param("firstName",firstName)
                        .param("lastName",lastName)
                        .with(csrf()))
                .andDo(print())
                .andExpect(flash().attributeExists("error")) //error happened
                .andExpect(redirectedUrl("/signUp"))
                .andExpect(unauthenticated())
                .andExpect(status().isFound());

        //check that new user account does not exist
        assertTrue(userRepository.findByEmail(userEmail).isEmpty());
    }
}
