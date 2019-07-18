package com.progmatic.recordislandbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SimilarArtistsDTO {
    
    @JsonProperty("artist")
    private List<ArtistDto> artists;

    public SimilarArtistsDTO() {
    }

    public SimilarArtistsDTO(List<ArtistDto> artists) {
        this.artists = artists;
    }

    public List<ArtistDto> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistDto> artists) {
        this.artists = artists;
    }
}
