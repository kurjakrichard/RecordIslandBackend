/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.dto.RegistrationDto;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import com.progmatic.recordislandbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author balza
 */
@RestController
public class UserController {

    private UserService userService;

    @PostMapping(path = "/register")
    public ResponseEntity register(RegistrationDto registration) {

        try {
            userService.createUser(registration);
        } catch (AlreadyExistsException ex) {
 
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}
