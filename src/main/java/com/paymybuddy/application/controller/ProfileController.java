package com.paymybuddy.application.controller;

import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.repository.UserRepository;
import com.paymybuddy.application.service.BankAccountService;
import com.paymybuddy.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Profile controller
 */
@Controller
public class ProfileController {

    private final PrincipalInfoFactory principalInfoFactory;
    private final UserService userService;
    private final BankAccountService bankAccountService;

    @Autowired
    public ProfileController(PrincipalInfoFactory principalInfoFactory, UserService userService, BankAccountService bankAccountService) {
        this.principalInfoFactory = principalInfoFactory;
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    /**
     * Shows profile page
     * @param principal
     * @param model
     * @return
     * @throws PrincipalAuthenticationException
     */
    @GetMapping("/profile")
    public String showProfileForm(Principal principal , Model model) throws PrincipalAuthenticationException {
        PrincipalInfo principalInfo = principalInfoFactory.getPrincipalInfo(principal);
        User user = userService.getPrincipalByEmail(principalInfo.getEmail());
        model.addAttribute("user",user);
        model.addAttribute("bankAccount",new BankAccount());
        model.addAttribute("bankTransferDto",new BankTransferDto());
        return "profile";
    }


}
