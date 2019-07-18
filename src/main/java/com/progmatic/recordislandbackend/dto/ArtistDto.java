package com.progmatic.recordislandbackend.dto;

/**
 *
 * @author Dano
 */
public class ArtistDto {
    String name;

    public ArtistDto() {
    }

    public ArtistDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
