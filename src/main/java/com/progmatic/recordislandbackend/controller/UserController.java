package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.dto.RegistrationDto;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import com.progmatic.recordislandbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping(path = "/register")
    public ResponseEntity register(@RequestBody RegistrationDto registration) {
        try {
            userService.createUser(registration);
        } catch (AlreadyExistsException ex) {
            
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
