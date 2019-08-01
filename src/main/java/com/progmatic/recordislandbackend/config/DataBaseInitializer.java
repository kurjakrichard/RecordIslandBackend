package com.progmatic.recordislandbackend.config;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.domain.Authority;
import com.progmatic.recordislandbackend.dto.ArtistDto;
import com.progmatic.recordislandbackend.dto.RegistrationDto;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import com.progmatic.recordislandbackend.exception.ArtistNotExistsException;
import com.progmatic.recordislandbackend.exception.LastFmException;
import com.progmatic.recordislandbackend.service.AllMusicWebScrapeService;
import com.progmatic.recordislandbackend.service.ArtistService;
import com.progmatic.recordislandbackend.service.LastFmServiceImpl;
import com.progmatic.recordislandbackend.service.UserService;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DataBaseInitializer {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private UserService userService;
    @Autowired
    private AllMusicWebScrapeService allMusicWebscrapeService;
    @Autowired
    private LastFmServiceImpl lastFmServiceImpl;
    @Autowired
    private ArtistService artistService;

    private Logger logger = LoggerFactory.getLogger(DataBaseInitializer.class);

    @Transactional
    public void init() throws AlreadyExistsException {
        if (em.createQuery("SELECT aut FROM Authority aut").getResultList().isEmpty()) {
            em.persist(new Authority("ROLE_USER"));
            em.persist(new Authority("ROLE_ADMIN"));
            em.flush();
            RegistrationDto registration = new RegistrationDto("admin", "admin", "admin@recordisland.com");
            registration.setLastFmUsername("life_is_fun");
            userService.createUser(registration, true);
        }
    }

    @EventListener(classes = ContextRefreshedEvent.class)
    public void onAppStartup(ContextRefreshedEvent ev) throws AlreadyExistsException, LastFmException, ArtistNotExistsException {
        DataBaseInitializer dbInitializer = ev.getApplicationContext().getBean(DataBaseInitializer.class);
        dbInitializer.init();
//        dbInitializer.getAllmusicRecommendations();
    }

//    @Scheduled(cron = "0 32 9 * * ? 2019")
    @Transactional
    public void getAllmusicRecommendations() throws ArtistNotExistsException {
        if (em.createQuery("SELECT COUNT(alb.id) FROM Album alb", Long.class).getSingleResult() == 0) {
            Set<Album> allMusicReleases = allMusicWebscrapeService.getAllMusicReleases();
            for (Album allMusicRelease : allMusicReleases) {
                try {
                    Artist artist = em.createQuery("SELECT art FROM Artist art WHERE art.name = :name", Artist.class).setParameter("name", allMusicRelease.getArtist().getName()).getSingleResult();
                    allMusicRelease.setArtist(artist);

                } catch (NoResultException ex) {
                    em.persist(allMusicRelease.getArtist());
                    em.flush();
                    try {
                        Set<ArtistDto> similarArtists;
                        similarArtists = lastFmServiceImpl.listSimilarArtists(allMusicRelease.getArtist().getName());
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
    }
}
