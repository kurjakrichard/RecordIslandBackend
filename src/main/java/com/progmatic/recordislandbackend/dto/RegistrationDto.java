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
    @NotNull
    private String lastFmUsername;
    @NotNull
    private boolean hasNewsLetter;

    public RegistrationDto() {
    }

    public RegistrationDto(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public RegistrationDto(String username, String password, String email, String lastFmUsername, boolean hasNewsLetter) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.lastFmUsername = lastFmUsername;
        this.hasNewsLetter = hasNewsLetter;
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

    public String getLastFmUsername() {
        return lastFmUsername;
    }

    public void setLastFmUsername(String lastFmUsername) {
        this.lastFmUsername = lastFmUsername;
    }

    public boolean hasNewsLetter() {
        return hasNewsLetter;
    }

    public void setNewsLetter(boolean hasNewsLetter) {
        this.hasNewsLetter = hasNewsLetter;
    }
}
