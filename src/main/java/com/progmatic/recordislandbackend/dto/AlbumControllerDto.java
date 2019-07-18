package com.progmatic.recordislandbackend.dto;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Dano
 */
public class AlbumControllerDto {

    @NotNull
    @Size(min = 2, message = "Username must be at least {2} characters long!")
    String title;
    @NotNull
    @Size(min = 2, message = "Username must be at least {2} characters long!")
    String artistName;
    LocalDate releaseDate;

    public AlbumControllerDto() {
        this.releaseDate = LocalDate.now();
    }

    public AlbumControllerDto(String title, String artistName) {
        this.title = title;
        this.artistName = artistName;
        this.releaseDate = LocalDate.now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

}
