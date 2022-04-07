package com.paymybuddy.application.service;

import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

/**
 * Bank account service
 */
@Service
public class BankAccountService {

    private BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    /**
     * Gets bank account by ID. Bank account is expected to exist in database.
     * @param id id of the bank account
     * @return a bankAccount instance
     * @throws NotFoundException when no bank account exists in database with given id
     */
    public BankAccount getById(int id) throws NotFoundException {
        Optional<BankAccount> bankAccountResult = bankAccountRepository.findById(id);
        if(bankAccountResult.isPresent()){
            return bankAccountResult.get();
        } else {
            throw new NotFoundException("Bank account not found");
        }
    }

    /**
     * Saves bank account into database. Can be use to create (null id) ar update (not null id) a bank account
     * @param bankAccount bank account instance to save
     * @return the saved bank account.
     */
    public BankAccount saveBankAccount(BankAccount bankAccount){
        return bankAccountRepository.save(bankAccount);
    }

    /**
     * Deletes a bank account by Id.
     * @param id id of the bank account
     */
    public void deleteBankAccount(int id){
        bankAccountRepository.deleteById(id);
    }
}
