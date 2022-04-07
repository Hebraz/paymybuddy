package com.paymybuddy.application.service;

import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.repository.BankAccountRepository;
import com.paymybuddy.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Locale;
import java.util.Optional;

/**
 * User service
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public UserService(UserRepository userRepository, BankAccountRepository bankAccountRepository) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    /**
     * Saves user into database. Can be use to create (null id) or update (not null id) an user
     * @param user user instance to save
     * @return the saved user.
     */
    public User saveUser(User user){
        return userRepository.save(user);
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
}
