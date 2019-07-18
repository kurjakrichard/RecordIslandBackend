package com.progmatic.recordislandbackend.dto;

import java.util.List;

public class TopArtistsDTO {
    private List<ArtistDto> artist;

    public List<ArtistDto> getArtist() {
        return artist;
    }

    public void setArtist(List<ArtistDto> artist) {
        this.artist = artist;
    }

    public TopArtistsDTO() {
    }
}
