package com.paymybuddy.application.controller;

import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.dto.BankTransferDto;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class BankTransferController {

    private final UserService userService;
    private final PrincipalInfoFactory principalInfoFactory;

    @Autowired
    public BankTransferController(UserService userService, PrincipalInfoFactory principalInfoFactory) {
        this.userService = userService;
        this.principalInfoFactory = principalInfoFactory;
    }

    @PostMapping("/bankTransfer")
    public String executeBankTransfer(
            HttpServletRequest request,
            @Valid @ModelAttribute BankTransferDto bankTransferDto) throws PrincipalAuthenticationException, NotFoundException, ForbiddenOperationException {

        PrincipalInfo principalInfo = principalInfoFactory.getPrincipalInfo(request.getUserPrincipal());
        userService.executeBankTransfer(principalInfo.getEmail(), bankTransferDto);

        return "redirect:/profile";
    }
}
