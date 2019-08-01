package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.config.RecordIslandProperties;
import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.dto.ArtistDto;
import com.progmatic.recordislandbackend.dto.SimilarArtistsWrapperDTO;
import com.progmatic.recordislandbackend.dto.TagsWrapperDTO;
import com.progmatic.recordislandbackend.dto.TopArtistsWrapperDTO;
import com.progmatic.recordislandbackend.exception.LastFmException;
import com.progmatic.recordislandbackend.exception.UserNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class LastFmServiceImpl {

    private final RecordIslandProperties properties;

    @PersistenceContext
    private EntityManager em;

    private final UserService userService;

    @Autowired
    public LastFmServiceImpl(RecordIslandProperties properties, UserService userService) {
        this.properties = properties;
        this.userService = userService;
    }

    public List<String> listGenres() {
        RestTemplate rt = new RestTemplate();

        HttpHeaders requestHeaders = new HttpHeaders();

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https").host("ws.audioscrobbler.com")
                .path("/2.0/").queryParam("method", "chart.gettoptags")
                .queryParam("api_key", properties.getLastFmApiKey()).queryParam("format", "json").build();

        HttpEntity request = new HttpEntity(requestHeaders);
        ResponseEntity<TagsWrapperDTO> response = rt.exchange(uriComponents.toUriString(),
                HttpMethod.GET,
                request,
                TagsWrapperDTO.class);

        TagsWrapperDTO tags = response.getBody();
        List<String> genres = tags.getTags().getTag().stream().map(t -> t.getName()).collect(Collectors.toList());

        return genres;
    }

    public Set<ArtistDto> listSimilarArtists(String name) throws LastFmException {
        RestTemplate rt = new RestTemplate();

        HttpHeaders requestHeaders = new HttpHeaders();

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https").host("ws.audioscrobbler.com")
                .path("/2.0/").queryParam("method", "artist.getsimilar").queryParam("artist", name).queryParam("autocorrect", "1")
                .queryParam("api_key", properties.getLastFmApiKey()).queryParam("format", "json").build();
        System.out.println(uriComponents.toUriString());
        HttpEntity request = new HttpEntity(requestHeaders);
        HttpEntity<SimilarArtistsWrapperDTO> response = rt.exchange(uriComponents.toUriString(),
                HttpMethod.GET,
                request,
                SimilarArtistsWrapperDTO.class);
        SimilarArtistsWrapperDTO simartists = response.getBody();
        if (simartists.hasErrors()) {
            throw new LastFmException("Artist not found on last.fm!");
        }
//        Set<Artist> result = new HashSet<>();
//        for (ArtistDto simartist : simartists.getSimilarArtists().getArtists()) {
//            result.add(new Artist(simartist.getName()));
//        }
//        List<String> result = simartists.getSimilarArtists().getArtists().stream().map(a -> a.getName()).collect(Collectors.toList());
        return new HashSet<>(simartists.getSimilarArtists().getArtists());
    }

    public List<String> listTopArtistsByGenre(String genre) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders requestHeaders = new HttpHeaders();

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https").host("ws.audioscrobbler.com")
                .path("/2.0/").queryParam("method", "tag.gettopartists").queryParam("tag", genre)
                .queryParam("api_key", properties.getLastFmApiKey()).queryParam("format", "json").build();

        System.out.println(uriComponents.toUriString());

        HttpEntity request = new HttpEntity(requestHeaders);
        HttpEntity<TopArtistsWrapperDTO> response = rt.exchange(uriComponents.toUriString(),
                HttpMethod.GET,
                request,
                TopArtistsWrapperDTO.class);

        TopArtistsWrapperDTO topartists = response.getBody();
        List<String> result = topartists.getTopartists().getArtist().stream().map(a -> a.getName()).collect(Collectors.toList());
        return result;
    }

    public List<String> getLastFmHistory(String username) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders requestHeaders = new HttpHeaders();

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https").host("ws.audioscrobbler.com")
                .path("/2.0/").queryParam("method", "user.gettopartists").queryParam("user", username)
                .queryParam("api_key", properties.getLastFmApiKey()).queryParam("format", "json").build();

        System.out.println(uriComponents.toUriString());

        HttpEntity request = new HttpEntity(requestHeaders);
        HttpEntity<TopArtistsWrapperDTO> response = rt.exchange(uriComponents.toUriString(),
                HttpMethod.GET,
                request,
                TopArtistsWrapperDTO.class);

        TopArtistsWrapperDTO topartists = response.getBody();
        return topartists.getTopartists().getArtist().stream().map(a -> a.getName()).collect(Collectors.toList());
    }

    @Transactional
    public void saveLastFmHistory(List<String> artists) throws UserNotFoundException {
        User user = userService.getLoggedInUserForTransactionsWithRecommendationsAndLikedArtistsAndDislikedArtists();
        
        artists.stream().forEachOrdered((artistName) -> {
            Artist artist;
            try {
                artist = em.createQuery("SELECT a FROM Artist a WHERE a.name = :artistName", Artist.class).setParameter("artistName", artistName).getSingleResult();
            } catch (NoResultException ex) {
                artist = new Artist(artistName);
                em.persist(artist);
            }
            user.addArtistToLikedArtistsFromLastFm(artist);
        });
    }

    @Transactional
    public void saveLastFmHistory(List<String> artists, String username) {
        User user = (User) userService.loadUserByUsername(username);

        artists.stream().forEachOrdered((artistName) -> {
            Artist artist;
            try {
                artist = em.createQuery("SELECT a FROM Artist a WHERE a.name = :artistName", Artist.class).setParameter("artistName", artistName).getSingleResult();
            } catch (NoResultException ex) {
                artist = new Artist(artistName);
                em.persist(artist);
            }
            user.addArtistToLikedArtistsFromLastFm(artist);
        });
    }

}
