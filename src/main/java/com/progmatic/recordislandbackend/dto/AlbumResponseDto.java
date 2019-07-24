/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.dto;

import com.progmatic.recordislandbackend.domain.Album;

/**
 *
 * @author Dano
 */
public class AlbumResponseDto {
    private String name;
    private String artist;
    private String img;

    public AlbumResponseDto() {
    }

    public AlbumResponseDto(String name, String artist, String img) {
        this.name = name;
        this.artist = artist;
        this.img = img;
    }
     
    public static AlbumResponseDto from(Album album) {
        return new AlbumResponseDto(album.getTitle(), album.getArtist().getName(), album.getImg());
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
