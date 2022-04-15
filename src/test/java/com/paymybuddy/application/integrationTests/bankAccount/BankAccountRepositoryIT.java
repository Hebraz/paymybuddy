package com.paymybuddy.application.integrationTests.bankAccount;

import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.repository.BankAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql({"/schema.sql", "/data.sql"}) //RELOAD database before each test
public class BankAccountRepositoryIT {

    @Autowired private BankAccountRepository bankAccountRepository;

    @Test
    void injectedComponentsAreNotNull(){
        assertThat(bankAccountRepository).isNotNull();
    }

    @Test
    void findByIdAndByUserEmailIdDoesNotExist(){
        Optional<BankAccount> bankAccount = bankAccountRepository.findByIdAndByUserEmail(88, "john.boyd@gmail.com");
        assertTrue(bankAccount.isEmpty());
    }

    @Test
    void findByIdAndByUserEmailBadUser(){
        Optional<BankAccount> bankAccount = bankAccountRepository.findByIdAndByUserEmail(88, "peter.duncan@gmail.com");
        assertTrue(bankAccount.isEmpty());
    }

    @Test
    void findByIdAndByUserEmailNominal(){
        Optional<BankAccount> bankAccount = bankAccountRepository.findByIdAndByUserEmail(1, "john.boyd@gmail.com");
        assertTrue(bankAccount.isPresent());
    }
}
