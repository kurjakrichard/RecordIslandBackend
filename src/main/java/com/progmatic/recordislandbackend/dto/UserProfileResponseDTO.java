package com.progmatic.recordislandbackend.dto;

public class UserProfileResponseDTO {
    
    private String username;
    private String email;
    private boolean newsLetter;
    private String lasFmUsername;

    public UserProfileResponseDTO(String username, String email, boolean newsLetter, String lasFmUsername) {
        this.username = username;
        this.email = email;
        this.newsLetter = newsLetter;
        this.lasFmUsername = lasFmUsername;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public boolean isNewsLetter() {
        return newsLetter;
    }

    public String getLasFmUsername() {
        return lasFmUsername;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNewsLetter(boolean newsLetter) {
        this.newsLetter = newsLetter;
    }

    public void setLasFmUsername(String lasFmUsername) {
        this.lasFmUsername = lasFmUsername;
    }
}
