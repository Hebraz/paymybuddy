package com.paymybuddy.application.controller;

import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.BankAccount;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

/**
 * Profile controller
 */
@Controller
public class ProfileController {

    private final PrincipalInfoFactory principalInfoFactory;
    private final UserService userService;

    @Autowired
    public ProfileController(PrincipalInfoFactory principalInfoFactory, UserService userService) {
        this.principalInfoFactory = principalInfoFactory;
        this.userService = userService;
    }

    /**
     * Shows profile page
     * @param principal user principal
     * @param model view model
     * @return profile page
     * @throws PrincipalAuthenticationException when an authentication failure is raised
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
