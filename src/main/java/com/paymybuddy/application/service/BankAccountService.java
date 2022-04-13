package com.paymybuddy.application.service;

import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.model.BankAccount;
import org.springframework.stereotype.Service;

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
     * Saves bank account into database. Can be use to create (null id) ar update (not null id) a bank account
     * @param bankAccount bank account instance to save
     * @return the saved bank account.
     */
    BankAccount saveBankAccount(BankAccount bankAccount);

    /**
     * Deletes a bank account by Id.
     * @param id id of the bank account
     */
    void deleteBankAccount(int id);


    /**
     * Adds a transfer to bank account
     * @param bankTransferDto bank transfer data from front
     * @throws NotFoundException when bank account does not exist in database
     */
    void addTransfer(BankTransferDto bankTransferDto) throws NotFoundException;
}
