package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.dao.ArtistRepository;
import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.dto.AlbumResponseDto;
import com.progmatic.recordislandbackend.dto.ArtistDto;
import com.progmatic.recordislandbackend.exception.ArtistNotExistsException;
import com.progmatic.recordislandbackend.exception.LastFmException;
import com.progmatic.recordislandbackend.exception.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @PersistenceContext
    private EntityManager em;
    private final LastFmServiceImpl lastFmService;
    private final DiscogsService discogsService;
    private final AllMusicWebScrapeService allmusicWebscrapeService;
    private final UserService userService;
    private final AlbumService albumService;
    private final ArtistService artistService;
    private final ArtistRepository artistRepository;
    private Logger logger = LoggerFactory.getLogger(RecommendationsServiceImpl.class);

    @Autowired
    public RecommendationsServiceImpl(LastFmServiceImpl lastFmService, DiscogsService discogsService,
            AllMusicWebScrapeService allmusicWebscrapeService, UserService userService, AlbumService albumService,
            ArtistService artistService, ArtistRepository artistRepository) {
        this.lastFmService = lastFmService;
        this.discogsService = discogsService;
        this.allmusicWebscrapeService = allmusicWebscrapeService;
        this.userService = userService;
        this.albumService = albumService;
        this.artistService = artistService;
        this.artistRepository = artistRepository;
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
            if (similarArtists != null && (loggedInUser.getLikedArtists().stream().map(a -> a.getArtist().getName()).anyMatch(a -> a.equals(album.getArtist().getName()))
                    || loggedInUser.getLikedArtists().stream().map(a -> a.getArtist().getName()).anyMatch(a -> similarArtists.stream().anyMatch(artist -> artist.getName().equals(a))))) {
                resultSet.add(album);
            }
        }
        return resultSet;
    }

    //@Scheduled(cron = "0 44 13 * * ?")
    @Transactional
    public void getAllmusicReleasesFromAllMusicDotCom() throws ArtistNotExistsException {

        Set<Album> allMusicReleases = allmusicWebscrapeService.getAllMusicReleases();
        for (Album allMusicRelease : allMusicReleases) {
            try {
                Artist artist = em.createQuery("SELECT art FROM Artist art WHERE art.name = :name", Artist.class).setParameter("name", allMusicRelease.getArtist().getName()).getSingleResult();
                allMusicRelease.setArtist(artist);

            } catch (NoResultException ex) {
                em.persist(allMusicRelease.getArtist());
                em.flush();
                try {
                    Set<ArtistDto> similarArtists;
                    similarArtists = lastFmService.listSimilarArtists(allMusicRelease.getArtist().getName());
                    Set<Artist> artists = new HashSet<>();
                    for (ArtistDto similarArtist : similarArtists) {
                        try {
                            Artist currentArtist = artistService.findArtistByName(similarArtist.getName());
                            artists.add(currentArtist);
                        } catch (ArtistNotExistsException ex1) {
                            Artist newArtist = new Artist(similarArtist.getName());
                            em.persist(newArtist);
                            em.flush();
                            artists.add(newArtist);
                        }
                    }
                    allMusicRelease.getArtist().setSimilarArtists(artists);
                } catch (LastFmException ex1) {
                    logger.debug(ex1.getMessage());
                }

            }
            em.persist(allMusicRelease);
        }

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
            if (similarArtists != null && (loggedInUser.getLikedArtists().stream().map(a -> a.getArtist().getName()).anyMatch(a -> a.equals(album.getArtist().getName()))
                    || loggedInUser.getLikedArtists().stream().map(a -> a.getArtist().getName()).anyMatch(a -> similarArtists.contains(a)))) {
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
            if (similarArtists != null && !loggedInUser.getPastAlbumRecommendations().contains(album)
                    && (loggedInUser.getLikedArtists().stream().map(a -> a.getArtist().getName()).anyMatch(a -> a.equals(album.getArtist().getName()))
                    || loggedInUser.getLikedArtists().stream().map(a -> a.getArtist().getName()).anyMatch(a -> similarArtists.contains(a)))) {
                tempAlbumRecommendations.add(album);
            }
        }
        
        loggedInUser.addAlbumsToAlbumRecommendations(tempAlbumRecommendations);
        loggedInUser.addAlbumsToPastAlbumRecommendations(tempAlbumRecommendations);
        loggedInUser.setLastRecommendationUpdate(LocalDateTime.now());
    }
    //same as below, only without parameter
//    @Transactional
//    public void updateLoggedinUsersAlbumRecommendations() throws UserNotFoundException {
//        User loggedInUser = userService.getLoggedInUserForTransactionsWithRecommendationsAndLikedArtistsAndDislikedArtists();
//        List<Album> allmusicAlbums = albumService.getAllAlbumsWithSimilarArtistsReleasedAfterLoggedInUsersLastRecommendationUpdate(LocalDateTime.now());
//        Set<Album> tempAlbumRecommendations = new HashSet<>();
//        for (Album album : allmusicAlbums) {
//            Set<String> similarArtists = album.getArtist().getSimilarArtists().stream().map(Artist::getName).collect(Collectors.toSet());
//            if (similarArtists != null && !loggedInUser.getPastAlbumRecommendations().contains(album)
//                    && (loggedInUser.getLikedArtists().stream().map(a -> a.getName()).anyMatch(a -> a.equals(album.getArtist().getName()))
//                    || loggedInUser.getLikedArtists().stream().map(a -> a.getName()).anyMatch(a -> similarArtists.contains(a)))) {
//                tempAlbumRecommendations.add(album);
//            }
//        }
//        
//        loggedInUser.addAlbumsToAlbumRecommendations(tempAlbumRecommendations);
//        loggedInUser.addAlbumsToPastAlbumRecommendations(tempAlbumRecommendations);
//        loggedInUser.setLastRecommendationUpdate(LocalDateTime.now());
//    }

    @Transactional
    public void updateUsersAlbumRecommendations(User user) throws UserNotFoundException {
        List<Album> allmusicAlbums = albumService.getAllAlbumsWithSimilarArtistsReleasedAfterLoggedInUsersLastRecommendationUpdate(LocalDateTime.now());
        Set<Album> tempAlbumRecommendations = new HashSet<>();
        for (Album album : allmusicAlbums) {
            Set<String> similarArtists = album.getArtist().getSimilarArtists().stream().map(Artist::getName).collect(Collectors.toSet());
            if (similarArtists != null && !user.getPastAlbumRecommendations().contains(album)
                    && (user.getLikedArtists().stream().map(a -> a.getArtist().getName()).anyMatch(a -> a.equals(album.getArtist().getName()))
                    || user.getLikedArtists().stream().map(a -> a.getArtist().getName()).anyMatch(a -> similarArtists.contains(a)))) {
                tempAlbumRecommendations.add(album);
            }
        }
        user.addAlbumsToAlbumRecommendations(tempAlbumRecommendations);
        user.addAlbumsToPastAlbumRecommendations(tempAlbumRecommendations);
        user.setLastRecommendationUpdate(LocalDateTime.now());
    }

    @Transactional
    public void addArtistToLikedArtistsOfLoggedInUser(Artist artist) throws UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactions();
        loggedInUser.addArtistToLikedArtists(artist);
    }
    
   
    

    @Transactional
    public void removeAlbumFromAlbumRecommendationsOfLoggedinUser(Album album) throws UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactions();
        loggedInUser.removeAlbumFromAlbumRecommendations(album);
    }
    
    @Transactional
    public void removeArtistFromLikedArtistsOfLoggedinUser(Artist artist) throws UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactions();
        loggedInUser.removeArtistFromLikedArtists(artist);
    }

    @Transactional
    public void addArtistToUsersDislikedArtists(Artist artist) throws UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactions();
        loggedInUser.addArtistToDislikedArtists(artist);
    }

    @Transactional
    public Set<AlbumResponseDto> getRecommendationsOfLoggedInUser() throws UserNotFoundException, LastFmException {
        User loggedInUser = userService.getLoggedInUserForTransactionsWithRecommendationsAndLikedArtistsAndDislikedArtists();
        Set<AlbumResponseDto> resultSet = new HashSet<>();
        updateUsersAlbumRecommendations(loggedInUser);
        for (Album albumRecommendation : loggedInUser.getAlbumRecommendations()) {
            resultSet.add(AlbumResponseDto.from(albumRecommendation));
        }
        return resultSet;
    }
    
    @Transactional
    public void addTopArtistsFromLastFmToLoggedInUser(List<String> topArtists) throws UserNotFoundException {
        User loggedInUser = userService.getLoggedInUserForTransactionsWithRecommendationsAndLikedArtistsAndDislikedArtists();
        for (String topArtist : topArtists) {
            if (artistRepository.existsByName(topArtist)) {
            loggedInUser.addArtistToLikedArtists(artistRepository.findByName(topArtist).get());
            } 
        }
    }
}
