package com.paymybuddy.application.controller;

import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.dto.TransactionDto;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.Transaction;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.service.TransactionService;
import com.paymybuddy.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    PrincipalInfo principalInfo;
    @Mock
    PrincipalInfoFactory principalInfoFactory;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    UserService userService;
    @Mock
    TransactionService transactionService;
    @Mock
    RedirectAttributes redirectAttributes;
    @Mock
    Model model;
    @Mock
    Principal principal;


    TransactionController transactionController;

    @BeforeEach
    void initializeTest(){
        transactionController = new TransactionController(userService,principalInfoFactory,transactionService);
    }
    @Test
    void showTransferFormNoParameter() throws PrincipalAuthenticationException {
        String email = "pierre.paul.oc@gmail.com";
        User userInDb = new User(email,"pwd","Pierre", "Paul", 0);
        ArgumentCaptor<PageRequest> pageRequest= ArgumentCaptor.forClass(PageRequest.class);
        Page<Transaction> page = getPage(5, 18); // => 4 pages
        //PREPARE
        when(principalInfo.getEmail()).thenReturn(email);
        when(principalInfoFactory.getPrincipalInfo(any())).thenReturn(principalInfo);
        when(userService.getPrincipalByEmail(any())).thenReturn(userInDb);
        when(transactionService.findPaginated(any(),any())).thenReturn(page);
        //ACT
        String htmlPage = transactionController.showTransferForm(model,principal, Optional.empty(),Optional.empty());
        //CHECK
        verify(transactionService, times(1)).findPaginated(eq(email) , pageRequest.capture());
        assertThat(pageRequest.getValue())
                .extracting(PageRequest::getPageNumber,PageRequest::getPageSize)
                .containsExactly(0,3);
        verify(model).addAttribute("transferPage",page );
        verify(model).addAttribute("user",userInDb );
        verify(model).addAttribute("pageNumbers",List.of(1,2,3,4));
        assertThat(htmlPage).isEqualTo("transfer");
    }

    @Test
    void showTransferFormWithParameter() throws PrincipalAuthenticationException {
        String email = "pierre.paul.oc@gmail.com";
        User userInDb = new User(email,"pwd","Pierre", "Paul", 0);
        ArgumentCaptor<PageRequest> pageRequest= ArgumentCaptor.forClass(PageRequest.class);
        Page<Transaction> page = getPage(10, 10); // => 1 pages
        //PREPARE
        when(principalInfo.getEmail()).thenReturn(email);
        when(principalInfoFactory.getPrincipalInfo(any())).thenReturn(principalInfo);
        when(userService.getPrincipalByEmail(any())).thenReturn(userInDb);
        when(transactionService.findPaginated(any(),any())).thenReturn(page);
        //ACT
        String htmlPage = transactionController.showTransferForm(model,principal, Optional.of(2),Optional.of(4));
        //CHECK
        verify(transactionService, times(1)).findPaginated(eq(email) , pageRequest.capture());
        assertThat(pageRequest.getValue())
                .extracting(PageRequest::getPageNumber,PageRequest::getPageSize)
                .containsExactly(1,4);
        verify(model).addAttribute("transferPage",page );
        verify(model).addAttribute("user",userInDb );
        verify(model).addAttribute("pageNumbers",List.of(1));
        assertThat(htmlPage).isEqualTo("transfer");
    }

    @Test
    void executeBankTransfer() throws PrincipalAuthenticationException, NotFoundException, ForbiddenOperationException {
        TransactionDto transactionDto = new TransactionDto("hello@openclassrooms.com", BigDecimal.valueOf(200.25));
        //PREPARE
        when(principalInfo.getEmail()).thenReturn("pierre.paul.oc@gmail.com");
        when(principalInfoFactory.getPrincipalInfo(any())).thenReturn(principalInfo);
        //ACT
        String page = transactionController.executeTransaction(httpServletRequest,redirectAttributes, transactionDto);
        //CHECK
        verify(userService,times(1)).executeTransaction("pierre.paul.oc@gmail.com", transactionDto);
        assertThat(page).isEqualTo("redirect:/transfer");
    }

    private Page<Transaction> getPage(int pageSize, int totalRecords)
    {
        return  new PageImpl<Transaction>(
                List.of(new Transaction(Instant.now(), 100, "Transfer 1", 10 ),
                        new Transaction(Instant.now().plusSeconds(300), 500, "Transfer 2", 50 ),
                        new Transaction(Instant.now().plusSeconds(600), 100050, "Transfer 3", 1000 )),
                Pageable.ofSize(pageSize),
                totalRecords);
    }

}