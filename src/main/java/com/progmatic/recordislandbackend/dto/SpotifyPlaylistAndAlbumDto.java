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
    String playlistName;
    String artist;
    String album;

    public SpotifyPlaylistAndAlbumDto(String playlistName, String artist, String album) {
        this.playlistName = playlistName;
        this.artist = artist;
        this.album = album;
    }

    public SpotifyPlaylistAndAlbumDto() {
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
    
    
}
