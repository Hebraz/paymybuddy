package com.paymybuddy.application.repository;

import com.paymybuddy.application.model.BankTransfer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankTransferRepository extends CrudRepository<BankTransfer, Integer> {
}
