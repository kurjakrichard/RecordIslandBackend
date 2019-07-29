package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.dto.RegistrationDto;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import com.progmatic.recordislandbackend.exception.EmailSendingException;
import com.progmatic.recordislandbackend.exception.UserNotFoundException;
import com.progmatic.recordislandbackend.service.UserService;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
public class UserController {

    private UserService userService;
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserController(UserService userService, ApplicationEventPublisher applicationEventPublisher) {
        this.userService = userService;
        this.eventPublisher = applicationEventPublisher;
    }

    @PostMapping(path = "/register")
    public ResponseEntity register(@Valid @RequestBody RegistrationDto registration, WebRequest request) throws AlreadyExistsException {
        User registered = userService.createUser(registration, false);
        userService.addUsersLastFmHistory(registration);
        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/verify")
    public ResponseEntity confirmRegistration(@RequestParam("token") final String token) {
        final String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification token is " + result + "!");
        }
    }

    @PostMapping(path = "/deleteUser")
    public ResponseEntity deleteUser(@Valid @RequestParam String username) throws UserNotFoundException {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }
}
