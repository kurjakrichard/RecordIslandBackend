/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.service;

import static com.progmatic.recordislandbackend.config.WebSecConfig.passwordEncoder;
import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Authority;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.dto.RegistrationDto;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import com.progmatic.recordislandbackend.exception.ArtistNotExistsExeption;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 *
 * @author balza
 */
@Service
public class AlbumService {

    @PersistenceContext
    private EntityManager em;

    
    
    @Transactional
    public void createAlbum(Album album) throws AlreadyExistsException, ArtistNotExistsExeption {
        if (albumExists(album.getTitle(), album.getArtist().getName())) {
            throw new AlreadyExistsException(album.getTitle());
        }
        
        if (!artistExists(album.getArtist().getName())) {
            throw new ArtistNotExistsExeption(album.getArtist().getName());
        }
        em.persist(album);
    }

    public boolean albumExists(String title, String artist) {
        Long num = em.createQuery("SELECT COUNT(u) FROM Album u WHERE u.title = :title AND u.artist = :artist", Long.class)
                .setParameter("title", title)
                .setParameter("artist", artist)
                .getSingleResult();

        return num == 1;
    }
        public boolean artistExists(String artist) {
        Long num = em.createQuery("SELECT COUNT(u) FROM Artist u WHERE u.name = :name", Long.class)
                .setParameter("artist", artist)
                .getSingleResult();

        return num == 1;
    }
}
