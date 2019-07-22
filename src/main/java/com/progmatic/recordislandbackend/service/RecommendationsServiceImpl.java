package com.progmatic.recordislandbackend.service;

import com.mysql.cj.conf.PropertyKey;
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

    private final LastFmServiceImpl lastFmService;
    private final DiscogsService discogsService;
    private final AllMusicWebScrapeService allmusicWebscrapeService;

    @Autowired
    public RecommendationsServiceImpl(LastFmServiceImpl lastFmService, DiscogsService discogsService, 
            AllMusicWebScrapeService allmusicWebscrapeService) {
        this.lastFmService = lastFmService;
        this.discogsService = discogsService;
        this.allmusicWebscrapeService = allmusicWebscrapeService;
    }

    public Set<Album> getDiscogsRecommendations() throws LastFmException {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HashSet<Album> resultSet = new HashSet();
        List<Album> discogsAlbums = discogsService.getDiscogsPage(2019, 4);

        for (Album album : discogsAlbums) {
            List<String> similarArtists;
            try {
                similarArtists = lastFmService.listSimilarArtists(album.getArtist().getName());
            } catch (LastFmException ex) {
                System.out.println(ex.getMessage() + album.getArtist().getName());
                continue;
            }
            if (similarArtists != null && (loggedInUser.getLikedArtists().stream().map(a -> a.getName()).anyMatch(a -> a.equals(album.getArtist().getName()))
                    || loggedInUser.getLikedArtists().stream().map(a -> a.getName()).anyMatch(a -> similarArtists.contains(a)))) {
                resultSet.add(album);
            }
        }
        return resultSet;

    }
    
    public Set<Album> getAllmusicRecommendations() throws LastFmException {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HashSet<Album> resultSet = new HashSet();
        Set<Album> allmusicAlbums = allmusicWebscrapeService.getAllMusicReleases();

        for (Album album : allmusicAlbums) {
            List<String> similarArtists;
            try {
                similarArtists = lastFmService.listSimilarArtists(album.getArtist().getName());
            } catch (LastFmException ex) {
                System.out.println(ex.getMessage() + album.getArtist().getName());
                continue;
            }
            if (similarArtists != null && (loggedInUser.getLikedArtists().stream().map(a -> a.getName()).anyMatch(a -> a.equals(album.getArtist().getName()))
                    || loggedInUser.getLikedArtists().stream().map(a -> a.getName()).anyMatch(a -> similarArtists.contains(a)))) {
                resultSet.add(album);
            }
        }
        return resultSet;

    }

    @Transactional
    @PatchMapping
    public void updateUserLikedArtists(Artist artist) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        loggedInUser.addArtistToLikedArtists(artist);
    }

}
