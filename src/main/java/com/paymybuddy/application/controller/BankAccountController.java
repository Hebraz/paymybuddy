package com.paymybuddy.application.controller;

import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.service.BankAccountService;
import com.paymybuddy.application.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Bank account controller
 */
@Controller
@Log4j2
public class BankAccountController {

    private final PrincipalInfoFactory principalInfoFactory;
    private final UserService userService;
    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(PrincipalInfoFactory principalInfoFactory, UserService userService, BankAccountService bankAccountService) {
        this.principalInfoFactory = principalInfoFactory;
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    /**
     * Creates a bank account
     * @param request the HttpServletRequest
     * @param bankAccount bank account to create
     * @return the page from which bank account creation has been made
     * @throws PrincipalAuthenticationException when principal cannot be identified
     */
    @PostMapping("/bankAccount")
    public String createBankAccount(
            HttpServletRequest request,
            @Valid @ModelAttribute BankAccount bankAccount) throws PrincipalAuthenticationException {

        PrincipalInfo principalInfo = principalInfoFactory.getPrincipalInfo(request.getUserPrincipal());
        userService.addBankAccount(principalInfo.getEmail(), bankAccount);

        //redirect to the page from which bank account creation has been made
        return "redirect:"+ request.getHeader("Referer");
    }

    /**
     * Deletes a bank account
     * @param request the HttpServletRequest
     * @param id id of the bank account
     * @return page from which bank account deletion has been made
     */
    @DeleteMapping("/bankAccount/{id}")
    public String deleteBankAccount(
            HttpServletRequest request,
            @PathVariable("id") int id){

        bankAccountService.deleteBankAccount(id);
        return "redirect:"+ request.getHeader("Referer");
    }

    /**
     * Shows bank account edition form
     * @param model
     * @param id id of the bank account to edit
     * @return bank account edition form
     * @throws NotFoundException when no bank account exists with provided id
     */
    @GetMapping("bankAccountEdit/{id}")
    public String showBankAccountEditForm(
            Model model,
            @PathVariable("id") int id) throws NotFoundException {

        BankAccount bankAccount = bankAccountService.getById(id);
        model.addAttribute("bankAccount",bankAccount);
        return "bankAccountEdit";
    }

    /**
     * Updates a bank account
     * @param request the HttpServletRequest
     * @param bankAccount bank account to be updated
     * @return page from which bank account update has been made
     */
    @PutMapping("/bankAccount")
    public String updateBankAccount(
            HttpServletRequest request,
            @Valid @ModelAttribute BankAccount bankAccount){

        bankAccountService.saveBankAccount(bankAccount);
        return "redirect:"+ request.getHeader("Referer");
    }
}
