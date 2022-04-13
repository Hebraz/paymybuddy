package com.paymybuddy.application.service;

import com.paymybuddy.application.model.ConnectionTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Transaction service interface
 */
public interface TransactionService {

    /**
     * Get list of transactions for user as a pagination
     * @param email user email
     * @param pageable pagination specification
     * @return list of transaction.
     */
    Page<ConnectionTransfer> findPaginated(String email, Pageable pageable);
}
