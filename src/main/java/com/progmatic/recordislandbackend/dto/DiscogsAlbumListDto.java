package com.progmatic.recordislandbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author Dano
 */
public class DiscogsAlbumListDto {
    
    @JsonProperty("results")
    private List<AlbumDto> albums;

    public DiscogsAlbumListDto() {
    }

    public DiscogsAlbumListDto(List<AlbumDto> albums) {
        this.albums = albums;
    }

    public List<AlbumDto> getAlbums() {
        return albums;
    }

    public void setAlbums(List<AlbumDto> albums) {
        this.albums = albums;
    }
    
    
}
