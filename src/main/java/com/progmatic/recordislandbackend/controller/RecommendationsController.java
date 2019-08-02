package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.dto.AlbumResponseDto;
import com.progmatic.recordislandbackend.exception.AlbumNotExistsException;
import com.progmatic.recordislandbackend.exception.LastFmException;
import com.progmatic.recordislandbackend.exception.UserNotFoundException;
import com.progmatic.recordislandbackend.service.AlbumService;
import com.progmatic.recordislandbackend.service.RecommendationsServiceImpl;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Dano
 */
@RestController
public class RecommendationsController {

    private final RecommendationsServiceImpl recommendationsService;
    private final AlbumService albumService;

    @Autowired
    public RecommendationsController(RecommendationsServiceImpl recommendationsService,
            AlbumService albumService) {
        this.recommendationsService = recommendationsService;
        this.albumService = albumService;
    }

    @Deprecated
    @GetMapping(value = {"/api/discogsRecommendation"})
    public Set<Album> getUserRecommendationsFromDiscogs() throws LastFmException {
        return recommendationsService.getDiscogsRecommendations();
    }

    @GetMapping(value = {"/api/allmusicRecommendation"})
    public List<AlbumResponseDto> getUserRecommendationsFromAllmusic() throws LastFmException, UserNotFoundException {
        return recommendationsService.getAllmusicRecommendationsFromDb();
    }

    @PostMapping(value = {"/api/userAlbumRecommendations/{id}"})
    public void handlePositiveFeedback(@PathVariable int id) throws UserNotFoundException, AlbumNotExistsException {
        Album album = albumService.findAlbumById(id);
        recommendationsService.addArtistToLikedArtistsOfLoggedInUser(album.getArtist());
        recommendationsService.removeAlbumFromAlbumRecommendationsOfLoggedinUser(album);
    }

    @DeleteMapping(value = {"/api/userAlbumRecommendations/{id}"})
    public void handleNegativeFeedback(@PathVariable int id) throws UserNotFoundException, AlbumNotExistsException {
        Album album = albumService.findAlbumById(id);
        recommendationsService.addArtistToUsersDislikedArtists(album.getArtist());
        recommendationsService.removeArtistFromLikedArtistsOfLoggedinUser(album.getArtist());
        recommendationsService.removeAlbumFromAlbumRecommendationsOfLoggedinUser(album);
    }

    @PatchMapping(value = {"/api/userAlbumRecommendations/{id}"})
    public void handleNeutralFeedback(@PathVariable int id) throws UserNotFoundException, AlbumNotExistsException {
        Album album = albumService.findAlbumById(id);
        recommendationsService.removeAlbumFromAlbumRecommendationsOfLoggedinUser(album);
    }
    
    @GetMapping(value = {"/api/userAlbumRecommendations"})
    public @ResponseBody
    Set<AlbumResponseDto> getAlbumRecommendationsOfLoggedInUser() throws UserNotFoundException, LastFmException {
        return recommendationsService.getRecommendationsOfLoggedInUser();
    }
    
    
}
