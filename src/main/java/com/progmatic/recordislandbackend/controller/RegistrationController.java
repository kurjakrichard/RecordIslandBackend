package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.dto.AlbumDto;
import com.progmatic.recordislandbackend.dto.GenreResponseDTO;
import com.progmatic.recordislandbackend.service.DiscogsService;
import com.progmatic.recordislandbackend.service.LastFmServiceImpl;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    private final LastFmServiceImpl lastFmServiceImpl;
    private final DiscogsService discogsService;

    @Autowired
    public RegistrationController(LastFmServiceImpl lastFmServiceImpl, DiscogsService discogsService) {
        this.lastFmServiceImpl = lastFmServiceImpl;
        this.discogsService = discogsService;
    }

    @GetMapping(path = "/api/genres")
    public List<GenreResponseDTO> listGenres() {
        List<GenreResponseDTO> response = lastFmServiceImpl.listGenres().stream().map(this::convertToDto).collect(Collectors.toList());
        System.out.println(response.size());
        return response;
    }

    @GetMapping(path = "/api/simartists")
    public List<String> listSimilarArtists(@RequestParam String name) {
        return lastFmServiceImpl.listSimilarArtists(name);
    }

    @GetMapping(path = "/api/topartists")
    public List<String> listTopArtistsByGenre(@RequestParam String genre) {
        return lastFmServiceImpl.listTopArtistsByGenre(genre);
    }

    @GetMapping(path = "/api/getlastfmhistory")
    public HttpStatus saveLastFmHistory(@RequestParam String username) {
        lastFmServiceImpl.saveLastFmHistory(lastFmServiceImpl.getLastFmHistory(username));
        return HttpStatus.OK;
    }

    @GetMapping(path = "/api/discogs")
    public List<Album> getDiscogsReleases(@RequestParam int year) {
        return discogsService.getDiscogsPageOne(year);
    }

    private GenreResponseDTO convertToDto(String name) {
        return new GenreResponseDTO(name);
    }
    

}
