package com.progmatic.recordislandbackend.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RegistrationDto {
    
    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 5, message = "Username must be at least {min} characters long!")
    private String username;
    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 8, message = "Password must be at least {min} characters long!")
    private String password;
    @NotNull
    @NotBlank
    @Email
    @NotEmpty
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
