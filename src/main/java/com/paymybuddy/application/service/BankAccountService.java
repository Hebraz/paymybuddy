package com.paymybuddy.application.service;

import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.model.BankAccount;

/**
 * Bank account service interface
 */
public interface BankAccountService {

    /**
     * Gets bank account by ID. Bank account is expected to exist in database.
     * @param id id of the bank account
     * @return a bankAccount instance
     * @throws NotFoundException when no bank account exists in database with given id
     */
    BankAccount getById(int id) throws NotFoundException;

    /**
     * Update bank account into database. Must be owned by given user (by email).
     * Cannot be use to create (null id) a bank account
     * @param bankAccount bank account instance to save
     * @param email user's email
     * @throws ForbiddenOperationException when a user try to save an account of another person
     * @throws IllegalArgumentException when trying to create a bank account (id null)
     * @return the saved bank account.
     */
    BankAccount updateBankAccount(BankAccount bankAccount, String email) throws ForbiddenOperationException;

    /**
     * Deletes a bank account by Id. Must be owned by given user (by email)
     * @param id id of the bank account
     * @param email user's email
     * @throws ForbiddenOperationException when a user try to delete account of another person
     */
    void deleteBankAccount(int id, String email) throws ForbiddenOperationException;


    /**
     * Adds a transfer to bank account
     * @param bankTransferDto bank transfer data from front
     * @throws NotFoundException when bank account does not exist in database
     */
    void addTransfer(BankTransferDto bankTransferDto) throws NotFoundException;

    /**
     * Checks if a given bank account is owned by a given user
     * @param bankAccountId id of the bank account
     * @param email user's email
     * @return true is user owns the bank account, false else.
     */
    public Boolean isBankAccountOwnedByUser(int bankAccountId, String email);
}
