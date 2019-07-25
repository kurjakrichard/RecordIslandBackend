package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.dto.AlbumResponseDto;
import com.progmatic.recordislandbackend.dto.ArtistDto;
import com.progmatic.recordislandbackend.exception.LastFmException;
import com.progmatic.recordislandbackend.exception.UserNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final UserService userService;
    private final AlbumService albumService;
    private final ArtistService artistService;

    @Autowired
    public RecommendationsServiceImpl(LastFmServiceImpl lastFmService, DiscogsService discogsService,
            AllMusicWebScrapeService allmusicWebscrapeService, UserService userService, AlbumService albumService,
            ArtistService artistService) {
        this.lastFmService = lastFmService;
        this.discogsService = discogsService;
        this.allmusicWebscrapeService = allmusicWebscrapeService;
        this.userService = userService;
        this.albumService = albumService;
        this.artistService = artistService;
    }

    public Set<Album> getDiscogsRecommendations() throws LastFmException {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HashSet<Album> resultSet = new HashSet();
        List<Album> discogsAlbums = discogsService.getDiscogsPage(2019, 4);

        for (Album album : discogsAlbums) {
            Set<ArtistDto> similarArtists;
            try {
                similarArtists = lastFmService.listSimilarArtists(album.getArtist().getName());
            } catch (LastFmException ex) {
                System.out.println(ex.getMessage() + album.getArtist().getName());
                continue;
            }
            if (similarArtists != null && (loggedInUser.getLikedArtists().stream().map(a -> a.getName()).anyMatch(a -> a.equals(album.getArtist().getName()))
                    || loggedInUser.getLikedArtists().stream().map(a -> a.getName()).anyMatch(a -> similarArtists.stream().anyMatch(artist -> artist.getName().equals(a))))) {
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
            Set<String> similarArtists;
            try {
                if (album.getArtist().getSimilarArtists().isEmpty()) {
                    similarArtists = lastFmService.listSimilarArtists(album.getArtist().getName()).stream().map(ArtistDto::getName).collect(Collectors.toSet());
                } else {
                    similarArtists = album.getArtist().getSimilarArtists().stream().map(Artist::getName).collect(Collectors.toSet());
                }
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
    
    public List<AlbumResponseDto> getAllmusicRecommendationsFromDb() throws LastFmException, UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactions();
        ArrayList<AlbumResponseDto> resultList = new ArrayList<>();
        List<Album> allmusicAlbums = albumService.getAllAlbumsFromDb();

        for (Album album : allmusicAlbums) {
            Set<String> similarArtists = album.getArtist().getSimilarArtists().stream().map(Artist::getName).collect(Collectors.toSet());


//            try {
//                if (album.getArtist().getSimilarArtists().isEmpty()) {
//                    similarArtists = lastFmService.listSimilarArtists(album.getArtist().getName()).stream().map(ArtistDto::getName).collect(Collectors.toSet());
//                    
//                } else {
//                    similarArtists = album.getArtist().getSimilarArtists().stream().map(Artist::getName).collect(Collectors.toSet());
//                }
//            } catch (LastFmException ex) {
//                System.out.println(ex.getMessage() + album.getArtist().getName());
//                continue;
//            }


            if (similarArtists != null && (loggedInUser.getLikedArtists().stream().map(a -> a.getName()).anyMatch(a -> a.equals(album.getArtist().getName()))
                    || loggedInUser.getLikedArtists().stream().map(a -> a.getName()).anyMatch(a -> similarArtists.contains(a)))) {
                resultList.add(AlbumResponseDto.from(album));
                addAlbumToAlbumRecommendationsOfLoggedInUser(album);
            }
        }
        return resultList;

    }

    @Transactional
    public void addArtistToLikedArtistsOfLoggedInUser(Artist artist) throws UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactions();
        loggedInUser.addArtistToLikedArtists(artist);
        em.persist(loggedInUser);
    }
    
    @Transactional
    public void addAlbumToAlbumRecommendationsOfLoggedInUser(Album album) throws UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactions();
        loggedInUser.addAlbumToAlbumRecommendations(album);
        em.persist(loggedInUser);
    }
    
    @Transactional
    public void removeAlbumFromAlbumRecommendationsOfLoggedinUser(Album album) throws UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactions();
        loggedInUser.removeAlbumFromAlbumRecommendations(album);
        em.persist(loggedInUser);
    }
    
    @Transactional
    public void addArtistToUsersDislikedArtists(Artist artist) throws UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactions();
        loggedInUser.addArtistToDislikedArtists(artist);
        em.persist(loggedInUser);
    }
    
    public Set<Album> getRecommendationsOfLoggedInUser() throws UserNotFoundException{
        return userService.getLoggedInUserForTransactions().getAlbumRecommendations();
    }
    
    
    

}
