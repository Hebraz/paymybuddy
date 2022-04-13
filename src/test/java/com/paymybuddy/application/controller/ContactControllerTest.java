package com.paymybuddy.application.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ContactControllerTest {


    @Test
    void showContactForm() {
        ContactController contactController = new ContactController();
        String page = contactController.showContactForm();
        assertThat(page).isEqualTo("contact");
    }
}