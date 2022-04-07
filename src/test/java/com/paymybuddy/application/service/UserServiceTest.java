package com.paymybuddy.application.service;

import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    private UserService userService;

    private User nominalUser;

    @BeforeEach
    void initializeTest(){
        userService = new UserService(userRepository);
        nominalUser =  new User("pierre.paul.oc@gmail.com","pwd", "Pierre","Paul",0);
    }

    @Test
    void saveUser() {
        //PREPARE
        when(userRepository.save(any(User.class))).thenReturn(nominalUser);
        //ACT
        User userToSave = new User();
        User savedUser = userService.saveUser(userToSave);

        //CHECK
        verify(userRepository, times(1)).save(userToSave);
        assertThat(savedUser).isEqualTo(nominalUser);
    }

    @Test
    void findByEmailPresent() {
        //PREPARE
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(nominalUser));
        //ACT
        Optional<User> returnedUserResult = userService.findByEmail("pierre.paul@gmail.com");

        //CHECK
        verify(userRepository, times(1)).findByEmail("pierre.paul@gmail.com");
        assertThat(returnedUserResult.get()).isEqualTo(nominalUser);
    }

    @Test
    void findByEmailEmpty() {
        //PREPARE
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());
        //ACT
        Optional<User> returnedUserResult = userService.findByEmail("pierre.paul@gmail.com");

        //CHECK
        verify(userRepository, times(1)).findByEmail("pierre.paul@gmail.com");
        assertTrue(returnedUserResult.isEmpty());
    }


    @Test
    void getPrincipalByEmailPresent() throws PrincipalAuthenticationException {
        //PREPARE
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(nominalUser));
        //ACT
        User returnedUser = userService.getPrincipalByEmail("pierre.paul@gmail.com");

        //CHECK
        verify(userRepository, times(1)).findByEmail("pierre.paul@gmail.com");
        assertThat(returnedUser).isEqualTo(nominalUser);
    }

    @Test
    void getPrincipalByEmailEmpty() {
        //PREPARE
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());
        //ACT
        assertThrows(PrincipalAuthenticationException.class,
                () ->  userService.getPrincipalByEmail("pierre.paul@gmail.com"));
    }

    @Test
    void addBankAccount() throws PrincipalAuthenticationException {
        //PREPARE
        String email = "pierre.paul@gmail.com";
        BankAccount bankAccount = new BankAccount("BNP","FR123456789");
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(nominalUser));

        //ACT
        userService.addBankAccount(email, bankAccount);

        //CHECK
        assertThat(nominalUser.getBankAccounts().get(0)).isEqualTo(bankAccount);
        verify(userRepository,times(1)).save(nominalUser);

    }
}