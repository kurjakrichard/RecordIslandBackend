package com.progmatic.recordislandbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimilarArtistsWrapperDTO {
    
    @JsonProperty("similarartists")
    private SimilarArtistsDTO similarArtists;

    public SimilarArtistsWrapperDTO() {
    }

    public SimilarArtistsDTO getSimilarArtists() {
        return similarArtists;
    }

    public void setSimilarArtists(SimilarArtistsDTO similarArtists) {
        this.similarArtists = similarArtists;
    }
}
