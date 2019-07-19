package com.progmatic.recordislandbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimilarArtistsWrapperDTO {
    
    @JsonProperty("similarartists")
    private SimilarArtistsDTO similarArtists;
    
    private int error = -1;
    
    private String message;

    public SimilarArtistsWrapperDTO() {
    }

    public SimilarArtistsDTO getSimilarArtists() {
        return similarArtists;
    }

    public void setSimilarArtists(SimilarArtistsDTO similarArtists) {
        this.similarArtists = similarArtists;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean hasErrors() {
        return error != -1;
    }
    
    
}
