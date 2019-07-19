package com.progmatic.recordislandbackend.dto;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Dano
 */
public class AlbumRatingDto {

    @NotNull
    @Size(min = 2, message = "Username must be at least {2} characters long!")
    String title;
    @NotNull
    @Size(min = 2, message = "Username must be at least {2} characters long!")
    String artistname;
    @NotNull
    boolean like;

    public AlbumRatingDto() {
    }

    public AlbumRatingDto(String title, String artistname, boolean like) {
        this.title = title;
        this.artistname = artistname;
        this.like = like;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtistname() {
        return artistname;
    }

    public void setArtistname(String artistname) {
        this.artistname = artistname;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }


}
