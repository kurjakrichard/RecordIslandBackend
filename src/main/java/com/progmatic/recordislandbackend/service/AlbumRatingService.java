/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.AlbumRating;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.dto.AlbumRatingDto;
import com.progmatic.recordislandbackend.exception.AlbumNotExistsException;
import com.progmatic.recordislandbackend.exception.ArtistNotExistsExeption;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 *
 * @author balza
 */
@Service
public class AlbumRatingService {

    @PersistenceContext
    private EntityManager em;
    private AlbumService albumService;
    private UserService userService;

    @Autowired
    public AlbumRatingService(AlbumService albumService, UserService userService) {
        this.albumService = albumService;
        this.userService = userService;
    }

    @Transactional
    public void createLike(AlbumRatingDto albumRatingDto) throws AlbumNotExistsException, ArtistNotExistsExeption {

        String title = albumRatingDto.getTitle();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = (User) userService.loadUserByUsername(username);
        if (!albumService.artistExists(albumRatingDto.getArtistname())) {
            throw new ArtistNotExistsExeption("The artist: " + albumRatingDto.getArtistname() + " not exist!");
        }
        if (!albumService.albumExists(albumRatingDto.getTitle(), albumRatingDto.getArtistname())) {
            throw new AlbumNotExistsException(albumRatingDto.getTitle());
        }
        System.out.println("jó-e?");

        System.out.println("Eddig jó!");

        System.out.println(user.getUsername());
        Album album = em.createQuery("SELECT a FROM Album a WHERE a.title = :title", Album.class)
                .setParameter("title", title)
                .getSingleResult();

        AlbumRating albumRating = new AlbumRating(album, albumRatingDto.isLike(), user);
        user.getAlbumRatings().add(albumRating);
        em.persist(albumRating);
    }

    public boolean ratingExists(String title, String artist) {
        Long num = em.createQuery("SELECT COUNT(u) FROM Album u WHERE u.title = :title AND u.artist.name = :artist", Long.class)
                .setParameter("title", title)
                .setParameter("artist", artist)
                .getSingleResult();

        return num == 1;
    }

}
