package com.progmatic.recordislandbackend.dto;

import java.util.List;

/**
 *
 * @author Dano
 */
public class DiscogsAlbumListDto {
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
