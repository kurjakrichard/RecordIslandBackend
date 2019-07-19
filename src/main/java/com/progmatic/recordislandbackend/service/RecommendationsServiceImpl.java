package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.User;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    public RecommendationsServiceImpl(LastFmServiceImpl lastFmService, DiscogsService discogsService) {
        this.lastFmService = lastFmService;
        this.discogsService = discogsService;
    }
    
    
    
    public Set<Album> getRecommendations() {
        List<Album> albums = discogsService.getDiscogsPageOne(2019);
        return null;
    }
    
}
