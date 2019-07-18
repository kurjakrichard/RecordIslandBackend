/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.dto.AlbumControllerDto;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import com.progmatic.recordislandbackend.exception.ArtistNotExistsExeption;
import com.progmatic.recordislandbackend.service.AlbumService;
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
public class AlbumController {

    private AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping(path = "/createalbum")
    public ResponseEntity createAlbum(@RequestBody AlbumControllerDto albumDto) throws ArtistNotExistsExeption {

        try {
            albumService.createAlbum(albumDto);
        } catch (AlreadyExistsException ex) {

            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

}
