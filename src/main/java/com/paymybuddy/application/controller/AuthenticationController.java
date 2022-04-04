package com.paymybuddy.application.controller;

import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * Authentication controller
 */
@Controller
public class AuthenticationController {

    private final UserService userService;
    private final  PrincipalInfoFactory principalInfoFactory;

    public AuthenticationController( UserService userService, PrincipalInfoFactory principalInfoFactory) {
        this.userService = userService;
        this.principalInfoFactory = principalInfoFactory;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping(value={"/", "/home"})
    public String showHome(Principal principal, Model model) {
        PrincipalInfo principalInfo = principalInfoFactory.getPrincipalInfo(principal);
        if(principalInfo != null){
            User user = userService.findByEmail(principalInfo.getEmail());

            //If user has been authenticated by oAuth2 and does not exist yet into paymybuddy DB,
            // create it into DB to manage its balance
            if((principalInfo.authenticationType() == PrincipalInfo.AuthenticationType.OAUTH2)  &&
                    (user==null)){
                user = new User(principalInfo.getEmail(), null, principalInfo.getFirstName(), principalInfo.getLastName(), 0);
                user =  userService.saveUser(user);
            }
            model.addAttribute("user", user);
        }

        return "home";
    }
}
