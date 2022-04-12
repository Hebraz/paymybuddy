package com.paymybuddy.application.controller;

import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Optional;

/**
 * Authentication controller
 */
@Controller
public class AuthenticationController {

    private final UserService userService;
    private final  PrincipalInfoFactory principalInfoFactory;

    @Autowired
    public AuthenticationController( UserService userService, PrincipalInfoFactory principalInfoFactory) {
        this.userService = userService;
        this.principalInfoFactory = principalInfoFactory;
    }

    /**
     * Shows login form
     * @return the login form
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    /**
     * Shows home page after authentication.
     * If user has been authenticated by oAuth2 for the first time, then it is registered into application database
     * @param principal user principal
     * @param model model
     * @return home page
     * @throws PrincipalAuthenticationException when principal is not authenticated or email of principal cannot be retrieved
     */
    @GetMapping(value={"/", "/home"})
    public String showHome(Principal principal, Model model) throws PrincipalAuthenticationException {
        User user;
        PrincipalInfo principalInfo = principalInfoFactory.getPrincipalInfo(principal);

        //User may not be in database
        Optional<User> userResult = userService.findByEmail(principalInfo.getEmail());

        //If user has been authenticated by oAuth2 and does not exist yet into paymybuddy DB,
        // create it into DB to manage its balance
        if ((principalInfo.authenticationType() == PrincipalInfo.AuthenticationType.OAUTH2) &&
                (userResult.isEmpty())) {
            user = new User(principalInfo.getEmail(), null, principalInfo.getFirstName(), principalInfo.getLastName(), 0);
            user = userService.createUser(user);
        } else if (userResult.isPresent()) {
            user = userResult.get();
        } else {
            throw new PrincipalAuthenticationException("Principal not registered in database");
        }

        model.addAttribute("user", user);
        return "home";
    }
}
