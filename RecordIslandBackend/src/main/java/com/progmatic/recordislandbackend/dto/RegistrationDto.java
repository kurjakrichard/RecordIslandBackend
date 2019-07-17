/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.dto;

import java.time.LocalDate;
import static javax.swing.text.StyleConstants.Size;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author balza
 */
public class RegistrationDto {
    
    @NotNull
    @Size(min = 5, message = "Username must be at least {2} characters long!")
    private String username;
    @NotNull
    @Size(min = 8, message = "Password must be at least {2} characters long!")
    private String password;
    @NotNull
    @NotBlank
    @Email
    private String email;

    public RegistrationDto() {
    }

    public RegistrationDto(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }    
}
