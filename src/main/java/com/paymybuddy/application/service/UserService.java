package com.paymybuddy.application.service;

import com.paymybuddy.application.contant.BankTransferType;
import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.dto.SignUpDto;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.Authority;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.repository.AuthorityRepository;
import com.paymybuddy.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * User service
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BankAccountService bankAccountService;
    private final AuthorityRepository authorityRepository;

    @Value("${paymybuddy.user_role}")
    private String USER_ROLE;

    @Autowired
    public UserService(UserRepository userRepository, BankAccountService bankAccountService, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.bankAccountService = bankAccountService;
        this.authorityRepository = authorityRepository;
    }

    /**
     * Updates user into database. Cannot be use to create a user (null id)
     * @param user user instance to update
     * @return the saved user.
     * @throws IllegalArgumentException when trying to update a non-existing user
     */
    public User updateUser(User user){
        if(Objects.nonNull(user.getId())) {
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Cannot update non existing user : " + user.getEmail());
        }
    }

    /**
     * Creates user into database. Cannot be use to update a user (non null id)
     * @param user user instance to create
     * @return the saved user.
     * @throws IllegalArgumentException when trying to create an existing user
     */
    public User createUser(User user){
        if(Objects.isNull(user.getId())) {
            //create Authority
            Optional<Authority> authorityResult = authorityRepository.findByAuthority(USER_ROLE);
            user.setAuthority(authorityResult.get());
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Cannot create existing user : " + user.getEmail());
        }
    }

    /**
     * Creates User Account
     */
    public User createUserAccount(SignUpDto signUpDto) throws ForbiddenOperationException {

        String email = signUpDto.getEmail();
        String emailConfirmation = signUpDto.getEmailConfirmation();
        String firstName = signUpDto.getFirstName();
        String lastName = signUpDto.getLastName();
        String password = signUpDto.getPassword();
        String encodedPassword = new BCryptPasswordEncoder().encode(password);

        //Check email
        if(email.equals(emailConfirmation)) {
            //Check that a user is not already registered with the same email
            // If it is the case, display an error, else create account
            Optional<User> userInDb = this.findByEmail(email);
            if (userInDb.isEmpty()) {
                User user = new User(email, encodedPassword, firstName, lastName, 0);
                return this.createUser(user);
            } else {
                throw new ForbiddenOperationException("An account with this email already exists");
            }
        }
        else {
            throw new ForbiddenOperationException("Emails are different");
        }
    }

    /**
     * Gets user by email. User is not supposed to exist in database.
     * @param email email of the user
     * @return an optional user instance
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase(Locale.ROOT));
    }

    /**
     * Gets principal user by email. User is expected to exist in database.
     * @param email email of the user
     * @return a user instance
     * @throws PrincipalAuthenticationException when no principal user exists in database with given email
     */
    public User getPrincipalByEmail(String email) throws PrincipalAuthenticationException {
        Optional<User> userResult = userRepository.findByEmail(email.toLowerCase(Locale.ROOT));
        if(userResult.isPresent())
        {
            return userResult.get();
        } else {
            throw new PrincipalAuthenticationException("No user exist in database with email : " + email);
        }
    }

    /**
     * Adds a bank account to a given user
     * @param userEmail email of the user
     * @param bankAccount bank account instance to add
     * @throws PrincipalAuthenticationException when no principal user exists in database with given email
     */
    @Transactional
    public void addBankAccount(String userEmail, BankAccount bankAccount) throws PrincipalAuthenticationException {
        //Check if a bank account with same IBAN does not already exist fro current user
        User user = getPrincipalByEmail(userEmail);
        user.getBankAccounts().add(bankAccount);
        userRepository.save(user);
    }

    /**
     * Executes a bank transfer: update user balance and register the transfer
     * @param userEmail email of user that executes the bank transfer
     * @param bankTransferDto a BankTransfer object
     * @throws ForbiddenOperationException when transfer would lead to negative balance or overflow
     * @throws NotFoundException when bank account does not exist in database
     * @throws PrincipalAuthenticationException when principal user is not identified
     */
    @Transactional
    public void executeBankTransfer(String userEmail, BankTransferDto bankTransferDto) throws ForbiddenOperationException, NotFoundException, PrincipalAuthenticationException {
        User user = getPrincipalByEmail(userEmail);
        long amountInCents = bankTransferDto.getAmount().multiply(BigDecimal.valueOf(100)).longValueExact(); //amount is validated at controller level. No overflow can occur here
        BankTransferType transferType = bankTransferDto.getTransferType();

        if(transferType == BankTransferType.DEBIT_MYBUDDY_ACCOUNT)
        {
            amountInCents = -amountInCents;
        }
        /*update user balance*/
        updateUserBalance(user,amountInCents);

        /*add bankTransfer*/
        bankAccountService.addTransfer(bankTransferDto);
    }


    /**
     * Update user balance with amount in cents.
     *
     * @param user a User instance
     * @param amountInCents positive integer in case of credit, negative integer in case of debit
     * @throws ForbiddenOperationException when overflow or negative balance is detected
     */
    private void updateUserBalance(User user, long amountInCents) throws ForbiddenOperationException {
        long balance = user.getBalance();

        //Check that balance will stay in range [0 .. Long.MAX_VALUE]
        if(amountInCents > Long.MAX_VALUE - balance) {
            throw new ForbiddenOperationException("Amount must be less than " + amountInCentToString(Long.MAX_VALUE - balance));
        } else if(amountInCents < -balance) {
            throw new ForbiddenOperationException("Amount must be less than " + amountInCentToString(balance));
        }
        balance += amountInCents;
        user.setBalance(balance);
        userRepository.save(user);
    }

    private String amountInCentToString(long amountInCents){
        BigDecimal amount = BigDecimal.valueOf(amountInCents).divide(BigDecimal.valueOf(100));
        return String.format(Locale.FRANCE, "%,.2f", amount);
    }
}
