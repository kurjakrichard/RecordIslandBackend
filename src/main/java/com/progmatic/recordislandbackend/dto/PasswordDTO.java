package com.progmatic.recordislandbackend.dto;

public class PasswordDTO {
    
    private int userID;
    private String password;

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
}
