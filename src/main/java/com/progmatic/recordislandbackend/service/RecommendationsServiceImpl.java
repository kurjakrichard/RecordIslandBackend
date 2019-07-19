package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.exception.LastFmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;

/**
 *
 * @author Dano
 */
@Service
public class RecommendationsServiceImpl {

    @PersistenceContext
    EntityManager em;

    LastFmServiceImpl lastFmService;
    DiscogsService discogsService;

    @Autowired
    public RecommendationsServiceImpl(LastFmServiceImpl lastFmService, DiscogsService discogsService) {
        this.lastFmService = lastFmService;
        this.discogsService = discogsService;
    }
    
    
    public Set<Album> getRecommendations() throws LastFmException {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HashSet<Album> resultSet = new HashSet();
        List<Album> discogsAlbums = discogsService.getDiscogsPage(2019, 4);
        
        try {
        for (Album album : discogsAlbums) {
            List<String> similarArtists = lastFmService.listSimilarArtists(album.getArtist().getName());
            if (loggedInUser.getLikedArtists().stream().map(a -> a.getName()).anyMatch(a -> a.equals(album.getArtist().getName())) || 
                    loggedInUser.getLikedArtists().stream().map(a -> a.getName()).anyMatch(a -> similarArtists.contains(a))) {
                resultSet.add(album);
            }
        }
        } catch(LastFmException le) {
           
        };
        return resultSet;

    }
    
    @Transactional
    @PatchMapping
    public void updateUserLikedArtists(Artist artist) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        loggedInUser.addArtistToLikedArtists(artist);
    }

}
