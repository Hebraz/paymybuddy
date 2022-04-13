package com.paymybuddy.application.service;

import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.dto.ConnectionDto;
import com.paymybuddy.application.dto.ConnectionTranferDto;
import com.paymybuddy.application.dto.SignUpDto;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.model.User;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * User service interface
 */
public interface UserService {



    /**
     * Updates user into database. Cannot be use to create a user (null id)
     * @param user user instance to update
     * @return the saved user.
     * @throws IllegalArgumentException when trying to update a non-existing user
     */
     User updateUser(User user);

    /**
     * Creates user into database. Cannot be used to update a user (non-null id)
     * @param user user instance to create
     * @return the saved user.
     * @throws IllegalArgumentException when trying to create an existing user
     */
    User createUser(User user);

    /**
     * Creates User Account
     */
    User createUserAccount(SignUpDto signUpDto) throws ForbiddenOperationException;

    /**
     * Gets user by email. User is not supposed to exist in database.
     * @param email email of the user
     * @return an optional user instance
     */
    Optional<User> findByEmail(String email);

    /**
     * Gets principal user by email. User is expected to exist in database.
     * @param email email of the user
     * @return a user instance
     * @throws PrincipalAuthenticationException when no principal user exists in database with given email
     */
    User getPrincipalByEmail(String email) throws PrincipalAuthenticationException;

    /**
     * Adds a bank account to a given user
     * @param userEmail email of the user
     * @param bankAccount bank account instance to add
     * @throws PrincipalAuthenticationException when no principal user exists in database with given email
     */
     void addBankAccount(String userEmail, BankAccount bankAccount)
             throws PrincipalAuthenticationException;

    /**
     * Executes a bank transfer: update user balance and register the transfer
     * @param userEmail email of user that executes the bank transfer
     * @param bankTransferDto a BankTransfer object
     * @throws ForbiddenOperationException when transfer would lead to negative balance or overflow
     * @throws NotFoundException when bank account does not exist in database
     * @throws PrincipalAuthenticationException when principal user is not identified
     */
     void executeBankTransfer(String userEmail, BankTransferDto bankTransferDto)
            throws ForbiddenOperationException, NotFoundException, PrincipalAuthenticationException;

     /**
     * Adds a connection to user
     * @param principalEmail email of active user.
     * @param connectionDto email of connection to add.
     */
    void addConnection(String principalEmail, ConnectionDto connectionDto)
            throws PrincipalAuthenticationException, NotFoundException;

    /**
     * Executes a bank transfer: update user balance and register the transfer
     * @param userEmail email of user that executes the bank transfer
     * @param connectionTransferDto a ConnectionTransferDto object
     * @throws ForbiddenOperationException when transfer would lead to negative balance or overflow
     * @throws NotFoundException when bank account does not exist in database
     * @throws PrincipalAuthenticationException when principal user is not identified
     */
     void executeConnectionTransfer(String userEmail, ConnectionTranferDto connectionTransferDto)
            throws ForbiddenOperationException, NotFoundException, PrincipalAuthenticationException;

}
