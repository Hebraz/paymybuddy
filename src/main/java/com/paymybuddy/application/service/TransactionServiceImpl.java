package com.paymybuddy.application.service;

import com.paymybuddy.application.model.ConnectionTransfer;
import com.paymybuddy.application.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Get list of transactions for user as a pagination
     * @param email user email
     * @param pageable pagination specification
     * @return list of transaction.
     */
    public Page<ConnectionTransfer> findPaginated(String email, Pageable pageable){
        return transactionRepository.findByPayerOrCreditEmail(email, pageable);
    }
}
