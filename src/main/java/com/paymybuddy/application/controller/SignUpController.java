package com.paymybuddy.application.controller;

import com.paymybuddy.application.dto.SignUpDto;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Sign up controller
 */
@Controller
@Log4j2
public class SignUpController {

    private final UserService userService;

    @Autowired
    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Show sign up page
     * @param model view model
     * @return sign up page
     */
    @GetMapping("/signUp")
    public String showSignUp(Model model ){
        model.addAttribute("signUpDto", new SignUpDto());
        return "signUp";
    }

    /**
     * Creates user account
     * @param request servlet request
     * @param model view model
     * @param signUpDto the signup information needed to create account
     * @return home if account creation succeed
     * @throws ForbiddenOperationException when user with given email already exist, or
     */
    @PostMapping("/signUp")
    public String createAccount( HttpServletRequest request,
                                 Model model,
                                 @Valid @ModelAttribute SignUpDto signUpDto) throws ForbiddenOperationException {

        userService.createUserAccount(signUpDto);

        //Authenticate user
        try {
            request.login(signUpDto.getEmail(), signUpDto.getPassword());
        } catch (ServletException e) {
            model.addAttribute("error","Authentication failed");
            log.error("Error while login: " + e.getMessage());
            return "redirect:/login";
        }
        return "redirect:/home";
    }
}
