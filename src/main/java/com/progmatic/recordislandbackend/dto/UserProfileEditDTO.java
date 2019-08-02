package com.progmatic.recordislandbackend.dto;

public class UserProfileEditDTO {
    
    private String lastFmUsername;
    private Boolean hasNewsLetter;

    public UserProfileEditDTO(String lastFmUsername, Boolean hasNewsLetter) {
        this.lastFmUsername = lastFmUsername;
        this.hasNewsLetter = hasNewsLetter;
    }

    public String getLastFmUsername() {
        return lastFmUsername;
    }

    public void setLastFmUsername(String lastFmUsername) {
        this.lastFmUsername = lastFmUsername;
    }

    public Boolean isHasNewsLetter() {
        return hasNewsLetter;
    }

    public void setHasNewsLetter(Boolean hasNewsLetter) {
        this.hasNewsLetter = hasNewsLetter;
    }
}
