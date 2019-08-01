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
public class SpotifyPlaylistAndAlbumDto {
    String playlistId;
    String artist;
    String album;

    public SpotifyPlaylistAndAlbumDto(String playlistName, String playlistId, String artist, String album) {
        this.playlistId = playlistId;
        this.artist = artist;
        this.album = album;
    }

    public SpotifyPlaylistAndAlbumDto() {
    }


    public String getPlaylistId() {
        return playlistId;
    }
    
    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }


    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
    
    
}
