package com.paymybuddy.application.controller;

import com.paymybuddy.application.controller.principalInfo.PrincipalInfo;
import com.paymybuddy.application.controller.principalInfo.PrincipalInfoFactory;
import com.paymybuddy.application.dto.TransactionDto;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import com.paymybuddy.application.model.Transaction;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.service.TransactionService;
import com.paymybuddy.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class TransactionController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final PrincipalInfoFactory principalInfoFactory;

    @Autowired
    public TransactionController(UserService userService, PrincipalInfoFactory principalInfoFactory, TransactionService transactionService) {
        this.userService = userService;
        this.principalInfoFactory = principalInfoFactory;
        this.transactionService = transactionService;
    }

    @GetMapping("/transfer")
    public String showTransferForm(Model model,
                                   Principal principal,
                                   @RequestParam("page") Optional<Integer> page,
                                   @RequestParam("size") Optional<Integer> size) throws PrincipalAuthenticationException {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(3);

        PrincipalInfo principalInfo = principalInfoFactory.getPrincipalInfo(principal);
        User user = userService.getPrincipalByEmail(principalInfo.getEmail());

        Page<Transaction> transferPage = transactionService.findPaginated(principalInfo.getEmail(), PageRequest.of(currentPage - 1, pageSize));

        int totalPages = transferPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("transferPage", transferPage);
        model.addAttribute("user",user);
        model.addAttribute("transactionDto" , new TransactionDto());
        return "transfer";
    }

    @PostMapping("/transfer")
    public String executeTransaction(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @Valid @ModelAttribute TransactionDto transactionDto) throws PrincipalAuthenticationException, NotFoundException, ForbiddenOperationException {

        PrincipalInfo principalInfo = principalInfoFactory.getPrincipalInfo(request.getUserPrincipal());
        userService.executeTransaction(principalInfo.getEmail(), transactionDto);
        redirectAttributes.addFlashAttribute("success","Transaction is completed");
        return "redirect:/transfer";
    }
}
