package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.dto.ArtistDto;
import com.progmatic.recordislandbackend.dto.GenreResponseDTO;
import com.progmatic.recordislandbackend.exception.ArtistNotExistsException;
import com.progmatic.recordislandbackend.exception.LastFmException;
import com.progmatic.recordislandbackend.exception.UserNotFoundException;
import com.progmatic.recordislandbackend.service.AllMusicWebScrapeService;
import com.progmatic.recordislandbackend.service.DiscogsService;
import com.progmatic.recordislandbackend.service.LastFmServiceImpl;
import com.progmatic.recordislandbackend.service.RecommendationsServiceImpl;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    private final LastFmServiceImpl lastFmServiceImpl;
    private final DiscogsService discogsService;
    private final AllMusicWebScrapeService allmusicWebscrapeService;
    private final RecommendationsServiceImpl recommendationsService;

    @Autowired
    public RegistrationController(LastFmServiceImpl lastFmServiceImpl, DiscogsService discogsService,
            AllMusicWebScrapeService allMusicWebScrapeService, RecommendationsServiceImpl recommendationsService) {
        this.lastFmServiceImpl = lastFmServiceImpl;
        this.discogsService = discogsService;
        this.allmusicWebscrapeService = allMusicWebScrapeService;
        this.recommendationsService = recommendationsService;
    }

    @GetMapping(path = "/api/genres")
    public List<GenreResponseDTO> listGenres() {
        List<GenreResponseDTO> response = lastFmServiceImpl.listGenres().stream().map(this::convertToDto).collect(Collectors.toList());
        return response;
    }

    @GetMapping(path = "/api/simartists")
    public Set<ArtistDto> listSimilarArtists(@RequestParam String name) throws LastFmException {
        return lastFmServiceImpl.listSimilarArtists(name);
    }

    @PostMapping(path = "/api/lastfmtopartists")
    public void updateLastFmProfile(@RequestBody List<String> genres) throws UserNotFoundException {
        for (String genre : genres) {
        recommendationsService.addTopArtistsFromLastFmToLoggedInUser(lastFmServiceImpl.listTopArtistsByGenre(genre));
        }
    }

    @GetMapping(path = "/api/discogs")
    public List<Album> getDiscogsReleases(@RequestParam int year) {
        return discogsService.getDiscogsPage(year, 1);
    }

    private GenreResponseDTO convertToDto(String name) {
        return new GenreResponseDTO(name);
    }
    
    @GetMapping(path = "/api/allmusic")
    public Set<Album> getDiscogsReleases() {
        return allmusicWebscrapeService.getAllMusicReleases();
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = {"/api/runWeeklyWebScrape"})
    public void getUserRecommendationsFromAllmusic() throws LastFmException, UserNotFoundException, ArtistNotExistsException {
        recommendationsService.getAllmusicReleasesFromAllMusicDotCom();
    }

}
