package com.paymybuddy.application.service;

import com.paymybuddy.application.contant.BankTransferType;
import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.dto.ConnectionDto;
import com.paymybuddy.application.dto.ConnectionTranferDto;
import com.paymybuddy.application.dto.SignUpDto;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.Authority;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.model.ConnectionTransfer;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.repository.AuthorityRepository;
import com.paymybuddy.application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
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
    @Mock
    AuthorityRepository authorityRepository;

    private UserService userService;

    private User nominalUser;

    private void mockAuthority(){
        //set userService properties
        ReflectionTestUtils.setField(userService, "USER_ROLE", "ROLE_USER");
        Authority authority = new Authority("ROLE_USER");
        when(authorityRepository.findByAuthority(any(String.class))).thenReturn(Optional.of(authority));
    }

    private void mockFeeRate(){
        ReflectionTestUtils.setField(userService, "FEE_RATE", BigDecimal.valueOf(0.05));
    }

    @BeforeEach
    void initializeTest(){
        userService = new UserServiceImpl(userRepository, bankAccountService, authorityRepository);
        nominalUser =  new User("pierre.paul.oc@gmail.com","pwd", "Pierre","Paul",0);
    }

    @Test
    void updateUserExistent(){
        User userToUpdate = new User();
        userToUpdate.setId(1); //make user exists
        User repositoryUser = new User("pierre.pau@gmail.com","pwd","Pierre", "Paul", 0);
        //PREPARE
        when(userRepository.save(userToUpdate)).thenReturn(repositoryUser);

        //ACT
        User returnedUser = userService.updateUser(userToUpdate);

        //CHECK
        verify(userRepository).save(userToUpdate);
        assertThat(returnedUser).isEqualTo(repositoryUser);
    }

    @Test
    void updateUserNonexistent(){
        User userToUpdate = new User();
         //ACT & CHECK
        assertThrows(IllegalArgumentException.class,() ->  userService.updateUser(userToUpdate));
    }

    @Test
    void createUserNonexistent() {
        //PREPARE
        when(userRepository.save(any(User.class))).thenReturn(nominalUser);
        mockAuthority();

        //ACT
        User userToSave = new User();
        User savedUser = userService.createUser(userToSave);

        //CHECK
        verify(userRepository, times(1)).save(userToSave);
        assertThat(savedUser).isEqualTo(nominalUser);
    }


    @Test
    void createExistent() {
        User userToCreate = new User();
        userToCreate.setId(1);
        //ACT & CHECK
        assertThrows(IllegalArgumentException.class,() ->  userService.createUser(userToCreate));
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

    @Test
    void createUserAccountEmailNotWellConfirmed(){
        SignUpDto signUpDto = new SignUpDto("toto@toto.com", "toti@toto.com","pwd","Toti","Toto");
        //ACT
        assertThrows(ForbiddenOperationException.class, () -> userService.createUserAccount(signUpDto));
    }

    @Test
    void createUserAccountUserAlreadyInDb(){
        String email = "toto@toto.com";
        SignUpDto signUpDto = new SignUpDto(email, email,"pwd","Toti","Toto");

        //PREPARE
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));
        //ACT
        assertThrows(ForbiddenOperationException.class, () -> userService.createUserAccount(signUpDto));
    }

    @Test
    void createUserAccountNominal() throws ForbiddenOperationException {
        String email = "toto@toto.com";
        String password = "pwd";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        SignUpDto signUpDto = new SignUpDto(email, email, password,"Toti","Toto");
        ArgumentCaptor<User> userProvidedToRepository = ArgumentCaptor.forClass(User.class);
        User savedUser = new User();

        //PREPARE
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(savedUser);
        mockAuthority();

        //ACT
        User returnedUser = userService.createUserAccount(signUpDto);

        //CHECK
        verify(userRepository).save(userProvidedToRepository.capture());

        assertThat(userProvidedToRepository.getValue())
                .extracting(User::getId,
                            User::getEmail,
                            User::getFirstName,
                            User::getLastName,
                            User::getBalance)
                .containsExactly(null, email, "Toti","Toto",0L);

        assertThat(returnedUser).isEqualTo(savedUser);
        assertTrue(passwordEncoder.matches(password, userProvidedToRepository.getValue().getPassword()));
    }

    @Test
    void addConnectionConnectionNonexistent(){

        String principalEmail = "principal@gmail.com";
        String connectionEmail = "connection@gmail.com";
        //PREPARE
        when(userRepository.findByEmail(principalEmail)).thenReturn(Optional.of(new User()));
        when(userRepository.findByEmail(connectionEmail)).thenReturn(Optional.empty());

        //CHECK
        assertThrows(NotFoundException.class, () -> userService.addConnection(principalEmail,new ConnectionDto(connectionEmail)));
    }

    @Test
    void addConnectionConnectionExistent() throws NotFoundException, PrincipalAuthenticationException {
        String principalEmail = "principal@gmail.com";
        String connectionEmail = "connection@gmail.com";
        User connectionUser = new User(connectionEmail, "pwd","Peter","Johns",12);
        User principalUser = new User(principalEmail, "pwd","Pierre","Paul",55);
        //PREPARE
        when(userRepository.findByEmail(principalEmail)).thenReturn(Optional.of(principalUser));
        when(userRepository.findByEmail(connectionEmail)).thenReturn(Optional.of(connectionUser));
        //ACT
        userService.addConnection(principalEmail,new ConnectionDto(connectionEmail));
        //CHECK
        assertSame(principalUser.getConnections().get(0), connectionUser);
        verify(userRepository).save(principalUser);
    }

    @Test
    void executeConnectionTransferConnectionNonExistent (){
        String principalEmail = "principal@gmail.com";
        String connectionEmail = "connection@gmail.com";
        //PREPARE
        when(userRepository.findByEmail(principalEmail)).thenReturn(Optional.of(new User()));
        when(userRepository.findByEmail(connectionEmail)).thenReturn(Optional.empty());

        //CHECK
        assertThrows(NotFoundException.class,
                () -> userService.executeConnectionTransfer(
                        principalEmail,
                        new ConnectionTranferDto(connectionEmail,BigDecimal.ZERO)));
    }

    @Test
    void executeConnectionTransferBalanceNotEnough (){
        String principalEmail = "principal@gmail.com";
        String connectionEmail = "connection@gmail.com";
        //PREPARE
        when(userRepository.findByEmail(principalEmail)).thenReturn(Optional.of(new User()));
        when(userRepository.findByEmail(connectionEmail)).thenReturn(Optional.empty());
        User connectionUser = new User(connectionEmail, "pwd","Peter","Johns",1000);
        User principalUser = new User(principalEmail, "pwd","Pierre","Paul",2000);
        ConnectionTranferDto tranferDto = new ConnectionTranferDto(connectionEmail,BigDecimal.valueOf(20.01));

        //CHECK
        assertThrows(NotFoundException.class,
                () -> userService.executeConnectionTransfer(
                        principalEmail,
                        tranferDto));
    }


    @Test
    void executeConnectionTransferNominal() throws NotFoundException, ForbiddenOperationException, PrincipalAuthenticationException {
        String principalEmail = "principal@gmail.com";
        String connectionEmail = "connection@gmail.com";
        User connectionUser = new User(connectionEmail, "pwd","Peter","Johns",1003);
        User principalUser = new User(principalEmail, "pwd","Pierre","Paul",2010);
        ConnectionTranferDto tranferDto = new ConnectionTranferDto(connectionEmail,BigDecimal.valueOf(5.02));
        //PREPARE
        when(userRepository.findByEmail(principalEmail)).thenReturn(Optional.of(principalUser));
        when(userRepository.findByEmail(connectionEmail)).thenReturn(Optional.of(connectionUser));
        mockFeeRate();
        //CHECK
        userService.executeConnectionTransfer(principalEmail,tranferDto);
        //CHECK
        verify(userRepository).save(principalUser);
        verify(userRepository).save(connectionUser);

        assertThat(principalUser.getBalance()).isEqualTo(1508);
        assertThat(connectionUser.getBalance()).isEqualTo(1505);

        ConnectionTransfer transfer = principalUser.getTransactionsAsPayer().get(0);
        assertThat(transfer)
                .extracting(ConnectionTransfer::getTotalAmount,
                        ConnectionTransfer::getFeeAmount)
                .containsExactly(502L, 25L);

        assertThat(transfer.getDate()).isBetween(Instant.now().minusSeconds(1), Instant.now());
    }
}