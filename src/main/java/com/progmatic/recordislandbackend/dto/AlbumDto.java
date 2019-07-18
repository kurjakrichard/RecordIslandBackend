package com.progmatic.recordislandbackend.dto;

import java.time.LocalDate;

/**
 *
 * @author Dano
 */
public class AlbumDto {
    String title;
    ArtistDto artist;
    LocalDate releaseDate;

    public AlbumDto() {
    }
    
    public AlbumDto(String title, ArtistDto artist, LocalDate releaseDate) {
        this.title = title;
        this.artist = artist;
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public ArtistDto getArtist() {
        return artist;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(ArtistDto artist) {
        this.artist = artist;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    
    
}
