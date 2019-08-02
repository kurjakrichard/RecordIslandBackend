package com.progmatic.recordislandbackend.dto;

public class UserProfileResponseDTO {
    
    private String username;
    private String email;
    private boolean newsLetter;
    private String lastFmUsername;

    public UserProfileResponseDTO(String username, String email, boolean newsLetter, String lastFmUsername) {
        this.username = username;
        this.email = email;
        this.newsLetter = newsLetter;
        this.lastFmUsername = lastFmUsername;
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

    public String getLastFmUsername() {
        return lastFmUsername;
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

    public void setLastFmUsername(String lastFmUsername) {
        this.lastFmUsername = lastFmUsername;
    }
}
