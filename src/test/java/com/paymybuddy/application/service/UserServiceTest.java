package com.paymybuddy.application.service;

import com.paymybuddy.application.contant.BankTransferType;
import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.repository.BankAccountRepository;
import com.paymybuddy.application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    BankAccountService bankAccountService;

    private UserService userService;

    private User nominalUser;

    @BeforeEach
    void initializeTest(){
        userService = new UserService(userRepository, bankAccountService);
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

    ///creditedAmount is constraint by controller validation in range [0 10^16-1]
    @ParameterizedTest(name = "initial balance in cents: ''{0}'', expected balance in cents ''{1}'', credited amount ''{2}''")
    @CsvSource({    "0,0.01,1",
                    "0, 999999999999999.99, 99999999999999999",
                    "9223372036854775806, 0.01, 9223372036854775807",
                    "9123372036854775809, 999999999999999.98, 9223372036854775807"
                } )
    void executeBankTransferNominalCredit(
            long userInitialBalanceInCents,
            BigDecimal creditedAmount,
            long userExpectedBalanceInCents) throws NotFoundException, ForbiddenOperationException, PrincipalAuthenticationException {

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        String email = "pierre.paul.oc@gmail.com";
        BankTransferDto bankTransferDto = new BankTransferDto(BankTransferType.CREDIT_MYBUDDY_ACCOUNT,1,creditedAmount);
        User userInDatabase = new User(email,"","Pierre","Paul",userInitialBalanceInCents);

        //PREPARE
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userInDatabase));

        //ACT
        userService.executeBankTransfer(email,bankTransferDto);

        //VERIFY
        verify(userRepository,times(1)).save(userArgumentCaptor.capture());
        verify(bankAccountService,times(1)).addTransfer(bankTransferDto);
        assertThat(userArgumentCaptor.getValue()).extracting(User::getBalance).isEqualTo(userExpectedBalanceInCents);
    }

    ///creditedAmount is constraint by controller validation in range [0 10^16-1]
    @ParameterizedTest(name = "initial balance in cents: ''{0}'', expected balance in cents ''{1}''")
    @CsvSource({ "9123372036854775809, 999999999999999.99" } )
    void executeBankTransferCreditOverflowThrowException(
            long userInitialBalanceInCents,
            BigDecimal creditedAmount)  {

        String email = "pierre.paul.oc@gmail.com";
        BankTransferDto bankTransferDto = new BankTransferDto(BankTransferType.CREDIT_MYBUDDY_ACCOUNT,1,creditedAmount);
        User userInDatabase = new User(email,"","Pierre","Paul",userInitialBalanceInCents);

        //PREPARE
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userInDatabase));

        //ACT
        assertThrows(ForbiddenOperationException.class, () -> userService.executeBankTransfer(email,bankTransferDto));
    }

    ///debitedAmount is constraint by controller validation in range [0 10^16-1]
    @ParameterizedTest(name = "initial balance in cents: ''{0}'', expected balance in cents ''{1}'', credited amount ''{2}''")
    @CsvSource({    "1,0.01,0",
                    "99999999999999999, 999999999999999.99, 0",
                    "9223372036854775807, 0.01, 9223372036854775806",
    } )
    void executeBankTransferNominalDebit(
            long userInitialBalanceInCents,
            BigDecimal creditedAmount,
            long userExpectedBalanceInCents) throws NotFoundException, ForbiddenOperationException, PrincipalAuthenticationException {

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        String email = "pierre.paul.oc@gmail.com";
        BankTransferDto bankTransferDto = new BankTransferDto(BankTransferType.DEBIT_MYBUDDY_ACCOUNT,1,creditedAmount);
        User userInDatabase = new User(email,"","Pierre","Paul",userInitialBalanceInCents);

        //PREPARE
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userInDatabase));

        //ACT
        userService.executeBankTransfer(email,bankTransferDto);

        //VERIFY
        verify(userRepository,times(1)).save(userArgumentCaptor.capture());
        verify(bankAccountService,times(1)).addTransfer(bankTransferDto);
        assertThat(userArgumentCaptor.getValue()).extracting(User::getBalance).isEqualTo(userExpectedBalanceInCents);
    }

    ///debitedAmount is constraint by controller validation in range [0 10^16-1]
    @ParameterizedTest(name = "initial balance in cents: ''{0}'', expected balance in cents ''{1}''")
    @CsvSource({ "0, 0.1", "99999999999999998, 999999999999999.99" } )
    void executeBankTransferDebitNegativeBalanceThrowException(
            long userInitialBalanceInCents,
            BigDecimal creditedAmount)  {

        String email = "pierre.paul.oc@gmail.com";
        BankTransferDto bankTransferDto = new BankTransferDto(BankTransferType.DEBIT_MYBUDDY_ACCOUNT,1,creditedAmount);
        User userInDatabase = new User(email,"","Pierre","Paul",userInitialBalanceInCents);

        //PREPARE
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userInDatabase));

        //ACT
        assertThrows(ForbiddenOperationException.class, () -> userService.executeBankTransfer(email,bankTransferDto));
    }
}