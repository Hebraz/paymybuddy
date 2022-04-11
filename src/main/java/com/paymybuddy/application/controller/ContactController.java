package com.paymybuddy.application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contact controller
 */
@Controller
public class ContactController {

    /**
     * Shows the contact page
     * @return the contact page
     */
    @GetMapping("/contact")
    public String showContactForm() {
        return "contact";
    }
}
