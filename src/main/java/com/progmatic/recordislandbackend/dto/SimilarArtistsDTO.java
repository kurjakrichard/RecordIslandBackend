package com.progmatic.recordislandbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SimilarArtistsDTO {
    
    @JsonProperty("artist")
    private List<Artist> artists;

    public SimilarArtistsDTO() {
    }

    public SimilarArtistsDTO(List<Artist> artists) {
        this.artists = artists;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }
}
