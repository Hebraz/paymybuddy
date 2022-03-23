package com.paymybuddy.application.repository;

import com.paymybuddy.application.model.BankAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, Integer> {
}
