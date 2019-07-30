/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.dto;

/**
 *
 * @author Dano
 */
public class SpotifyAlbumDto {
    String artist;
    String album;

    public SpotifyAlbumDto(String artist, String album) {
        this.artist = artist;
        this.album = album;
    }

    public SpotifyAlbumDto() {
    }
    
    

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
    
    
    
}
