package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.User;
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
    EntityManager em;
    
    LastFmServiceImpl lastFmService;
    DiscogsService discogsService;
    
    @Autowired
    public RecommendationsServiceImpl(EntityManager em, LastFmServiceImpl lastFmService, DiscogsService discogsService) {
        this.em = em;
        this.lastFmService = lastFmService;
        this.discogsService = discogsService;
    }
    
    
    
    public Set<Album> getRecommendations() {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return null;
    }
    
}
