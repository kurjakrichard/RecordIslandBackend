package com.progmatic.recordislandbackend.dto;

import java.time.LocalDate;

/**
 *
 * @author Dano
 */
public class AlbumDto {
    String title;
    int year;

    public AlbumDto() {
    }

    public AlbumDto(String title, int year) {
        this.title = title;
        this.year = year;
    }
    
    

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(int year) {
        this.year = year;
    }

    
    
    
    
}
