package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.dto.RegistrationDto;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import com.progmatic.recordislandbackend.service.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity register(@Valid @RequestBody RegistrationDto registration) throws AlreadyExistsException {
        userService.createUser(registration);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/deleteUser")
    public ResponseEntity deleteUser (@Valid @RequestParam String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }
}
