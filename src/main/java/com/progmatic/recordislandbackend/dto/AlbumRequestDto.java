/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.dto;

import com.progmatic.recordislandbackend.domain.Liked;

/**
 *
 * @author Dano
 */
public class AlbumRequestDto {
    private int id;
    private String name;
    private String artist;
    private boolean liked;

    public AlbumRequestDto() {
    }

    public AlbumRequestDto(int id, String name, String artist, boolean liked) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.liked = liked;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
    
    
}
