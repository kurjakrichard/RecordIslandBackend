package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.config.DataBaseInitializer;
import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.dto.AlbumResponseDto;
import com.progmatic.recordislandbackend.dto.ArtistDto;
import com.progmatic.recordislandbackend.exception.LastFmException;
import com.progmatic.recordislandbackend.exception.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dano
 */
@Service
public class RecommendationsServiceImpl {

    private final LastFmServiceImpl lastFmService;
    private final DiscogsService discogsService;
    private final AllMusicWebScrapeService allmusicWebscrapeService;
    private final UserService userService;
    private final AlbumService albumService;

    @Autowired
    public RecommendationsServiceImpl(LastFmServiceImpl lastFmService, DiscogsService discogsService,
            AllMusicWebScrapeService allmusicWebscrapeService, UserService userService, AlbumService albumService) {
        this.lastFmService = lastFmService;
        this.discogsService = discogsService;
        this.allmusicWebscrapeService = allmusicWebscrapeService;
        this.userService = userService;
        this.albumService = albumService;
    }

    @Deprecated
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

    //Weekly run 1 hour long db updater
    @Scheduled(cron = "0 26 9 * * ? 2019")
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

    @Transactional
    public List<AlbumResponseDto> getAllmusicRecommendationsFromDb() throws LastFmException, UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactionsWithRecommendationsAndLikedArtistsAndDislikedArtists();
        ArrayList<AlbumResponseDto> resultList = new ArrayList<>();
        List<Album> allmusicAlbums = albumService.getAllAlbumsWithSimilarArtists();

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
//                addAlbumToAlbumRecommendationsOfLoggedInUser(album);
            }
        }
        return resultList;
    }

    @Transactional
    public void updateLoggedinUsersAlbumRecommendations() throws UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactionsWithRecommendationsAndLikedArtistsAndDislikedArtists();
        List<Album> allmusicAlbums = albumService.getAllAlbumsWithSimilarArtistsReleasedAfterLoggedInUsersLastRecommendationUpdate(LocalDateTime.now());
        Set<Album> tempAlbumRecommendations = new HashSet<>();

        for (Album album : allmusicAlbums) {
            Set<String> similarArtists = album.getArtist().getSimilarArtists().stream().map(Artist::getName).collect(Collectors.toSet());
            if (similarArtists != null && (loggedInUser.getLikedArtists().stream().map(a -> a.getName()).anyMatch(a -> a.equals(album.getArtist().getName()))
                    || loggedInUser.getLikedArtists().stream().map(a -> a.getName()).anyMatch(a -> similarArtists.contains(a)))) {
                tempAlbumRecommendations.add(album);
            }
        }
        loggedInUser.addAlbumsToAlbumRecommendations(tempAlbumRecommendations);
        loggedInUser.setLastRecommendationUpdate(LocalDateTime.now());
    }

    @Transactional
    public void addArtistToLikedArtistsOfLoggedInUser(Artist artist) throws UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactions();
        loggedInUser.addArtistToLikedArtists(artist);
        userService.saveUser(loggedInUser);
    }

    @Transactional
    public void removeAlbumFromAlbumRecommendationsOfLoggedinUser(Album album) throws UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactions();
        loggedInUser.removeAlbumFromAlbumRecommendations(album);
        userService.saveUser(loggedInUser);
    }

    @Transactional
    public void addArtistToUsersDislikedArtists(Artist artist) throws UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactions();
        loggedInUser.addArtistToDislikedArtists(artist);
        userService.saveUser(loggedInUser);
    }

    @Transactional
    public Set<AlbumResponseDto> getRecommendationsOfLoggedInUser() throws UserNotFoundException, LastFmException {
        User actUser = userService.getLoggedInUserForTransactionsWithRecommendationsAndLikedArtistsAndDislikedArtists();
        Set<AlbumResponseDto> resultSet = new HashSet<>();
//        if (actUser.getAlbumRecommendations().isEmpty()) {
//            updateLoggedinUsersAlbumRecommendations();
//        }
        updateLoggedinUsersAlbumRecommendations();
        for (Album albumRecommendation : actUser.getAlbumRecommendations()) {
            resultSet.add(AlbumResponseDto.from(albumRecommendation));
        }
        return resultSet;
    }
    
    
}
