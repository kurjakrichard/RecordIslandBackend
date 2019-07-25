/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.dto.AlbumRatingDto;
import com.progmatic.recordislandbackend.exception.AlbumNotExistsException;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import com.progmatic.recordislandbackend.exception.ArtistNotExistsException;
import com.progmatic.recordislandbackend.service.AlbumRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author balza
 */
@RestController
public class RatingController {

    private AlbumRatingService albumRatingService;

    @Autowired
    public RatingController(AlbumRatingService albumRatingService) {
        this.albumRatingService = albumRatingService;
    }
    
    @PostMapping(path = "/createlike")
    public ResponseEntity createLike(@RequestBody AlbumRatingDto albumRatingDto) throws ArtistNotExistsException, AlbumNotExistsException, AlreadyExistsException{

            albumRatingService.createLike(albumRatingDto);
            
        return ResponseEntity.ok().build();
    }
    
        @PostMapping(path = "/editlike")
    public ResponseEntity editLike(@RequestBody AlbumRatingDto albumRatingDto) throws ArtistNotExistsException, AlbumNotExistsException {

            albumRatingService.editLike(albumRatingDto);
            
        return ResponseEntity.ok().build();
    }
}
