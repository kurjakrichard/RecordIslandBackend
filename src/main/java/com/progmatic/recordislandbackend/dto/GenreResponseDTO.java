package com.progmatic.recordislandbackend.dto;

public class GenreResponseDTO {
    
    private String name;

    public GenreResponseDTO() {
    }

    public GenreResponseDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
