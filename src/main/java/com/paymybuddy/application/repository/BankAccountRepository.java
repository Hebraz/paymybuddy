package com.paymybuddy.application.repository;

import com.paymybuddy.application.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Bank account repository
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {
    /**
     * Get bank account by IBAN
     * @param iban
     * @return optional bank account
     */
    Optional<BankAccount> findByIban(String iban);
}
