package com.progmatic.recordislandbackend.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PasswordDTO {
    
    private int userID;
    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 8, message = "Password must be at least {min} characters long!")
    private String password;
    private String token;

    public PasswordDTO() {
    }

    public PasswordDTO(int id, String password) {
        this.userID = id;
        this.password = password;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int id) {
        this.userID = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
}
