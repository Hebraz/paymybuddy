package com.paymybuddy.application.service;

import com.paymybuddy.application.model.Transaction;
import com.paymybuddy.application.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    TransactionRepository transactionRepository;

    @Test
    void findPaginated() {
        TransactionService transactionService= new TransactionServiceImpl(transactionRepository);
        Page<Transaction> page = createPage(3,25);
        Pageable pageable = PageRequest.of(2,10);
        String email = "toto@tata.com";
        //PREPARE
        when(transactionRepository.findByPayerOrCreditEmail(email, pageable)).thenReturn(page);
        //ACT
        Page<Transaction> returnedPage = transactionService.findPaginated(email, pageable);
        //CHECK
        verify(transactionRepository).findByPayerOrCreditEmail(email,pageable);
        assertThat(returnedPage).isEqualTo(page);


    }

    private Page<Transaction> createPage(int pageSize, int totalRecords)
    {
        return  new PageImpl<Transaction>(
                List.of(new Transaction(Instant.now(), 100, "Transfer 1", 10 ),
                        new Transaction(Instant.now().plusSeconds(300), 500, "Transfer 2", 50 ),
                        new Transaction(Instant.now().plusSeconds(600), 100050, "Transfer 3", 1000 )),
                Pageable.ofSize(pageSize),
                totalRecords);
    }
}