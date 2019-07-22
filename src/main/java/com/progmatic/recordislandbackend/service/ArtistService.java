/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.exception.LastFmException;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Dano
 */
@Service
public class ArtistService {
    EntityManager em;
    LastFmServiceImpl lastFmService;
    
    @Autowired
    public ArtistService(LastFmServiceImpl lastFmService) {
        this.lastFmService = lastFmService;
    }
    
    
    
    public Set<Artist> getAllArtistsFromDb() {
        return null;
    }
    
    public Set<Artist> getSimilarArtistsForArtist(Artist artist) throws LastFmException {
        HashSet<Artist> similarArtists = new HashSet<>();
        for (Artist similarArtist : lastFmService.listSimilarArtists(artist.getName())) {
            similarArtists.add(new Artist(similarArtist.getName()));
        }
        return similarArtists;
    }
    
    @Transactional
    public void addSimilarArtistsToArtist(Artist artist) throws LastFmException {
        artist.setSimilarArtists(getSimilarArtistsForArtist(artist));
    }
    
    
}
