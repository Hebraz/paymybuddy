package com.paymybuddy.application.repository;

import com.paymybuddy.application.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Bank account repository
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {
    /**
     * Get bank account by IBAN
     * @param iban bank account iban
     * @return optional bank account
     */
    Optional<BankAccount> findByIban(String iban);

    /**
     * Deletes a bank account by ID, provided that it owned by the given user
     * @param id account id to delete
     * @param email email of user that must own the bank account
     */
    @Modifying
    @Query("DELETE  " +
            "FROM BankAccount account " +
            "WHERE account IN " +
            " (SELECT user_account FROM User user " +
            " INNER JOIN user.bankAccounts user_account " +
            " WHERE user_account.id = :id AND user.email = :email)")
    void deleteByIdAndUserEmail(int id, String email);

    /**
     * Deletes a bank account by ID, provided that it owned by the given user
     * @param id account id to delete
     * @param email email of user that must own the bank account
     */
    @Query(" SELECT user_account FROM User user " +
            " INNER JOIN user.bankAccounts user_account " +
            " WHERE user_account.id = :id AND user.email = :email")
    Optional<BankAccount> findByIdAndByUserEmail(int id, String email);
}
