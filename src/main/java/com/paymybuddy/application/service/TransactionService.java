package com.paymybuddy.application.service;

import com.paymybuddy.application.model.Transaction;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.repository.TransactionRepository;
import com.paymybuddy.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Value("${paymybuddy.feerate}")
    private double feeRate;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Transaction registerTransaction(int payerId, int creditId, long amount, String description){

        /*compute transaction instant*/
        Instant transactionTimestamp = Instant.now();

        /*compute fee amount*/
        long feeAmount = Math.round((double)amount * feeRate);

        /*get payer and credit user*/
        User payer = userRepository.findById(payerId).orElseThrow();
        User credit = userRepository.findById(creditId).orElseThrow();

        Transaction transaction = new Transaction(transactionTimestamp,amount,description,feeAmount);
        transaction.setPayer(payer);
        transaction.setCredit(credit);
        return transactionRepository.save(transaction);
    }
}
