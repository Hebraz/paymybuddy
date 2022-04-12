package com.paymybuddy.application.repository;

import com.paymybuddy.application.model.ConnectionTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends CrudRepository<ConnectionTransfer,Integer> {

    @Query("SELECT transfer FROM ConnectionTransfer transfer " +
            "JOIN transfer.payer payer " +
            "JOIN transfer.credit credit " +
            "WHERE payer.email = :email  OR  credit.email = :email " +
            "ORDER BY transfer.date DESC")
    Page<ConnectionTransfer> findByPayerOrCreditEmail(String email, Pageable pageable);

}
