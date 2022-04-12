package com.paymybuddy.application.controller;

import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.dto.ConnectionDto;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.constraints.Email;
import java.security.Principal;

@Controller
@Log4j2
public class ConnectionController {

    private final UserService userService;
    private final PrincipalInfoFactory principalInfoFactory;

    @Autowired
    public ConnectionController(UserService userService, PrincipalInfoFactory principalInfoFactory) {
        this.userService = userService;
        this.principalInfoFactory = principalInfoFactory;
    }

    /**
     * Shows the adding connection page
     * @return page name
     */
    @GetMapping("/connection")
    public String showConnectionAddForm(Model model)  {
        model.addAttribute("connectionDto", new ConnectionDto());
        return "connection";
    }

    /**
     * Add a connection to the user
     * @param connectionDto user to be added as connection
     * @return connection transfer page
     */
    @PostMapping("/connection")
    public String addConnection(
            RedirectAttributes redirectAttributes,
            Principal principal,
            @Email @ModelAttribute ConnectionDto connectionDto) throws PrincipalAuthenticationException, NotFoundException {

        PrincipalInfo principalInfo = principalInfoFactory.getPrincipalInfo(principal);
        userService.addConnection(principalInfo.getEmail(), connectionDto);

        redirectAttributes.addFlashAttribute("success", connectionDto.getEmail() + " has been added.");
        return "redirect:transfer";
    }
}
