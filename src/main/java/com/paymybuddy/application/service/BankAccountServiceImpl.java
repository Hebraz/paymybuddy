package com.paymybuddy.application.service;

import com.paymybuddy.application.contant.BankTransferType;
import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.model.BankTransfer;
import com.paymybuddy.application.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
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





}
