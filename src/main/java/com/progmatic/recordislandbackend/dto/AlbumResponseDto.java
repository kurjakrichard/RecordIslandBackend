package com.progmatic.recordislandbackend.dto;

import com.progmatic.recordislandbackend.domain.Album;

/**
 *
 * @author Dano
 */
public class AlbumResponseDto {

    private int id;
    private String name;
    private String artist;
    private String img;

    public AlbumResponseDto() {
    }

    public AlbumResponseDto(int id, String name, String artist, String img) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.img = img;
    }

    public static AlbumResponseDto from(Album album) {
        return new AlbumResponseDto(album.getId(), album.getTitle(), album.getArtist().getName(), album.getImg());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
