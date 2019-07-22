/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.exception.LastFmException;
import com.progmatic.recordislandbackend.service.RecommendationsServiceImpl;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public Set<Album> getUserRecommendationsFromAllmusic() throws LastFmException {
        return recommendationsService.getAllmusicRecommendations();
    }
    
    
}
