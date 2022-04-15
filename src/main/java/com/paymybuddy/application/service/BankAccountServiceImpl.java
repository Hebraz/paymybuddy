package com.paymybuddy.application.service;

import com.paymybuddy.application.contant.BankTransferType;
import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.model.BankTransfer;
import com.paymybuddy.application.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Bank account service
 */
@Service
public class BankAccountServiceImpl implements  BankAccountService{

    private final BankAccountRepository bankAccountRepository;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository) {
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
     * Update bank account into database. Must be owned by given user (by email).
     * Cannot be use to create (null id) a bank account
     * @param bankAccount bank account instance to save
     * @param email user's email
     * @throws ForbiddenOperationException when a user try to save an account of another person
     * @throws IllegalArgumentException when trying to create a bank account (id null)
     * @return the saved bank account.
     */
    public BankAccount updateBankAccount(BankAccount bankAccount, String email) throws ForbiddenOperationException {
        Integer id = bankAccount.getId();
        if(Objects.nonNull(id)) {
            //Before updating bank account, check that it is owned by principal
            if(isBankAccountOwnedByUser(id,email)){
                return bankAccountRepository.save(bankAccount);
            } else {
                throw new ForbiddenOperationException(email + " cannot update this bank account");
            }
        } else {
                throw new IllegalArgumentException("Bank account cannot be created");
        }

    }

    /**
     * Deletes a bank account by Id. Must be owned by given user (by email)
     * @param id id of the bank account
     * @param email user's email
     * @throws ForbiddenOperationException when a user try to delete account of another person
     */
    public void deleteBankAccount(int id, String email) throws ForbiddenOperationException {
        //Before deleting bank account, check that it is owned by principal
        if(isBankAccountOwnedByUser(id,email)){
            bankAccountRepository.deleteById(id);
        } else {
            throw new ForbiddenOperationException(email + " does not own this bank account");
        }
    }


    /**
     * Adds a transfer to bank account
     * @param bankTransferDto bank transfer data from front
     * @throws NotFoundException when bank account does not exist in database
     */
    public void addTransfer(BankTransferDto bankTransferDto) throws NotFoundException {

        Optional<BankAccount> bankAccountResult = bankAccountRepository.findById(bankTransferDto.getBankId());
        if(bankAccountResult.isPresent())
        {
            BankAccount bankAccount = bankAccountResult.get();

            BankTransfer transfer = bankTransferFromDto(bankTransferDto);

            bankAccount.getBankTransfers().add(transfer);
            bankAccountRepository.save(bankAccount);
        } else {
            throw new NotFoundException("Operation failed.");
        }

    }

    /**
     * Create a BankTransfer object from a BankTransferDto
     * @param bankTransferDto a BankTransferDto instance
     * @return the corresponding BankTransfer
     */
    private BankTransfer bankTransferFromDto(BankTransferDto bankTransferDto){
        long amountInCents = bankTransferDto.getAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue(); //amount is validated at controller level. No overflow can occur here

        BankTransferType transferType = bankTransferDto.getTransferType();
        return new BankTransfer(Instant.now(), amountInCents, transferType.toString());
    }

    /**
     * Checks if a given bank account is owned by a given user
     * @param bankAccountId id of the bank account
     * @param email user's email
     * @return true is user owns the bank account, false else.
     */
    public Boolean isBankAccountOwnedByUser(int bankAccountId, String email){
        Optional<BankAccount> bankAccountResult = bankAccountRepository.findByIdAndByUserEmail(bankAccountId, email);
        return bankAccountResult.isPresent();
    }
}
