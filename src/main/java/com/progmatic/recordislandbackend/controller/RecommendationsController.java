package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.dto.AlbumResponseDto;
import com.progmatic.recordislandbackend.exception.LastFmException;
import com.progmatic.recordislandbackend.exception.UserNotFoundException;
import com.progmatic.recordislandbackend.service.RecommendationsServiceImpl;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Dano
 */
@RestController
public class RecommendationsController {
    
    private final RecommendationsServiceImpl recommendationsService;
    
    @Autowired
    public RecommendationsController(RecommendationsServiceImpl recommendationsService) {
        this.recommendationsService = recommendationsService;
    }
    
    @GetMapping(value = {"/api/discogsRecommendation"})
    public Set<Album> getUserRecommendationsFromDiscogs() throws LastFmException {
        return recommendationsService.getDiscogsRecommendations();
    }
    
    @GetMapping(value = {"/api/allmusicRecommendation"})
    public List<AlbumResponseDto> getUserRecommendationsFromAllmusic() throws LastFmException, UserNotFoundException {
        return recommendationsService.getAllmusicRecommendationsFromDb();
    }
    
    @PatchMapping(value = {"/api/userLikedArtists"})
    public void updateUsersLikedArtistsOfLoggedInUser(Artist artist) throws UserNotFoundException {
        recommendationsService.addArtistToLikedArtistsOfLoggedInUser(artist);
    }
    
    @PatchMapping(value = {"/api/userAlbumRecommendations"})
    public void addAlbumToAlbumRecommendationsOfLoggedInUser(Album album) throws UserNotFoundException {
        recommendationsService.addAlbumToAlbumRecommendationsOfLoggedInUser(album);
    }
    
//    @DeleteMapping(value = {"/api/userAlbumRecommendations"})
//    public void removeAlbumFromAlbumRecommendationsOfLoggedinUser(Album album) throws UserNotFoundException {
//        recommendationsService.removeAlbumFromAlbumRecommendationsOfLoggedinUser(album);
//    }
    
    @PatchMapping(value = {"/api/userDislikedArtists"})
    public void addArtistToDislikedArtistsOfLoggedInUser(Artist artist) throws UserNotFoundException {
        recommendationsService.addArtistToUsersDislikedArtists(artist);
    }
    
    @GetMapping(value = {"/api/userAlbumRecommendations"})
    public Set<Album> getAlbumRecommendationsOfLoggedInUser() throws UserNotFoundException {
        return recommendationsService.getRecommendationsOfLoggedInUser();
    }
    
}
