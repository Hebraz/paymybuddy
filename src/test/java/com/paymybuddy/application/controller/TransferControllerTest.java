package com.paymybuddy.application.controller;

import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.dto.ConnectionTranferDto;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.ConnectionTransfer;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

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


    TransferController transferController;

    @BeforeEach
    void initializeTest(){
        transferController = new TransferController(userService,principalInfoFactory,transactionService);
    }
    @Test
    void showTransferFormNoParameter() throws PrincipalAuthenticationException {
        String email = "pierre.paul.oc@gmail.com";
        User userInDb = new User(email,"pwd","Pierre", "Paul", 0);
        ArgumentCaptor<PageRequest> pageRequest= ArgumentCaptor.forClass(PageRequest.class);
        Page<ConnectionTransfer> page = getPage(5, 18); // => 4 pages
        //PREPARE
        when(principalInfo.getEmail()).thenReturn(email);
        when(principalInfoFactory.getPrincipalInfo(any())).thenReturn(principalInfo);
        when(userService.getPrincipalByEmail(any())).thenReturn(userInDb);
        when(transactionService.findPaginated(any(),any())).thenReturn(page);
        //ACT
        String htmlPage = transferController.showTransferForm(model,principal, Optional.empty(),Optional.empty());
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
        Page<ConnectionTransfer> page = getPage(10, 10); // => 1 pages
        //PREPARE
        when(principalInfo.getEmail()).thenReturn(email);
        when(principalInfoFactory.getPrincipalInfo(any())).thenReturn(principalInfo);
        when(userService.getPrincipalByEmail(any())).thenReturn(userInDb);
        when(transactionService.findPaginated(any(),any())).thenReturn(page);
        //ACT
        String htmlPage = transferController.showTransferForm(model,principal, Optional.of(2),Optional.of(4));
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
        ConnectionTranferDto connectionTranferDto = new ConnectionTranferDto("hello@openclassrooms.com", BigDecimal.valueOf(200.25));
        //PREPARE
        when(principalInfo.getEmail()).thenReturn("pierre.paul.oc@gmail.com");
        when(principalInfoFactory.getPrincipalInfo(any())).thenReturn(principalInfo);
        //ACT
        String page = transferController.executeBankTransfer(httpServletRequest,redirectAttributes,connectionTranferDto);
        //CHECK
        verify(userService,times(1)).executeConnectionTransfer("pierre.paul.oc@gmail.com", connectionTranferDto);
        assertThat(page).isEqualTo("redirect:/transfer");
    }

    private Page<ConnectionTransfer> getPage(int pageSize, int totalRecords)
    {
        return  new PageImpl<ConnectionTransfer>(
                List.of(new ConnectionTransfer(Instant.now(), 100, "Transfer 1", 10 ),
                        new ConnectionTransfer(Instant.now().plusSeconds(300), 500, "Transfer 2", 50 ),
                        new ConnectionTransfer(Instant.now().plusSeconds(600), 100050, "Transfer 3", 1000 )),
                Pageable.ofSize(pageSize),
                totalRecords);
    }

}