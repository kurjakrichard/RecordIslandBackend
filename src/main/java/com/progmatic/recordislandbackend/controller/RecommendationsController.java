package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.dto.AlbumResponseDto;
import com.progmatic.recordislandbackend.exception.LastFmException;
import com.progmatic.recordislandbackend.service.RecommendationsServiceImpl;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    public List<AlbumResponseDto> getUserRecommendationsFromAllmusic() throws LastFmException {
        return recommendationsService.getAllmusicRecommendationsFromDb();
    }
    
    
}
