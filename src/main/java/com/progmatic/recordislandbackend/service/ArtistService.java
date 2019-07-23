/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.domain.Artist_;
import com.progmatic.recordislandbackend.exception.LastFmException;
import com.progmatic.recordislandbackend.exception.UserNotFoundException;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Dano
 */
@Service
public class ArtistService {

    @PersistenceContext
    EntityManager em;
    LastFmServiceImpl lastFmService;

    @Autowired
    public ArtistService(LastFmServiceImpl lastFmService) {
        this.lastFmService = lastFmService;
    }

    public List<Artist> getAllArtistsFromDb() {
        return em.createQuery("SELECT a FROM Artist a").getResultList();
    }

//    public Set<ArtistDto> getSimilarArtistsForArtist(Artist artist) throws LastFmException {
//        HashSet<ArtistDto> similarArtists = new HashSet<>();
//        for (ArtistDto similarArtist : lastFmService.listSimilarArtists(artist.getName())) {
//            similarArtists.add(new ArtistDto(similarArtist.getName()));
//        }
//        return similarArtists;
//    }
    @Transactional
    public void addSimilarArtistsToArtist(String name, Set<Artist> artistSet) throws LastFmException, UserNotFoundException {
        Artist artist = findArtistByName(name);
        artist.setSimilarArtists(artistSet);
    }

    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Artist findArtistByName(String name) throws UserNotFoundException {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Artist> cQuery = cb.createQuery(Artist.class);
            Root<Artist> a = cQuery.from(Artist.class);
            cQuery.select(a).where(cb.equal(a.get(Artist_.name), name));

            return em.createQuery(cQuery).getSingleResult();
        } catch (NoResultException ex) {
            throw new UserNotFoundException("User with name " + name + " cannot be found!");
        }
    }

}
