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
public class SpotifyTrackResponseDto {
    String name;
    String previewUrl;

    public SpotifyTrackResponseDto() {
    }

    public SpotifyTrackResponseDto(String name, String previewUrl) {
        this.name = name;
        this.previewUrl = previewUrl;
    }

    public String getName() {
        return name;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
    
    
}
