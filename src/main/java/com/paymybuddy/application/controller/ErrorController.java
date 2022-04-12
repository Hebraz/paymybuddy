package com.paymybuddy.application.controller;

import com.paymybuddy.application.exception.ConflictException;
import com.paymybuddy.application.exception.ForbiddenOperationException;
import com.paymybuddy.application.exception.NotFoundException;
import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * Controller to handle all global errors as input validation,
 */
@ControllerAdvice
@Log4j2
public class ErrorController {

    @Value("${paymybuddy.general-client-error-message}")
    private String defaultErrorMessage;
    @Value("${paymybuddy.principal-authentication-error-message}")
    private String principalAuthenticationErrorMessage;

    /**
     * Input validation error handler
     *
     * @param ex Bind exception is raised when one or several inputs of a controller are not valid
     * @param request To retrieve the referer page in order to display error on it
     * @param redirectAttributes to inject error parameter into referer page
     * @return name of the page to be displayed on client
     */
    @ExceptionHandler(BindException.class)
    public String handleValidationExceptions(
            BindException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        String errorMsg =  ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect( Collectors.joining(". ") );

        log.error(errorMsg);
        redirectAttributes.addFlashAttribute("error",errorMsg);
        return "redirect:"+ request.getHeader("Referer");
    }

    /**
     * Principal identification error handler
     *
     * @param ex Principal exception
     * @param redirectAttributes to inject error parameter into referer page
     * @return name of the page to be displayed on client
     */
    @ExceptionHandler({PrincipalAuthenticationException.class})
    public String handlePrincipalExceptions(
            PrincipalAuthenticationException ex, RedirectAttributes redirectAttributes) {

        log.error(ex.getMessage());
        redirectAttributes.addFlashAttribute("error", principalAuthenticationErrorMessage);
        return "redirect:login";
    }

    /**
     * Application exceptions error handler
     *
     * @param ex Application exceptions
     * @param request To retrieve the referer page in order to display error on it
     * @param redirectAttributes to inject error parameter into referer page
     * @return name of the page to be displayed on client
     */
    @ExceptionHandler({ConflictException.class, NotFoundException.class, ForbiddenOperationException.class})
    public String handleApplicationExceptions(
            Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        log.error(ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:"+ request.getHeader("Referer");
    }

    /**
     * All other exceptions error handler
     *
     * @param ex all exceptions not handled by other handlers of this class
     * @param request To retrieve the referer page in order to display error on it
     * @param redirectAttributes to inject error parameter into referer page
     * @return name of the page to be displayed on client
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralExceptions(
            Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        log.error(ex.getMessage());
        redirectAttributes.addFlashAttribute("error", defaultErrorMessage);
        return "redirect:"+ request.getHeader("Referer");
    }
}
