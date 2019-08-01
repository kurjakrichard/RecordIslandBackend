package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.dto.SpotifyAlbumDto;
import com.progmatic.recordislandbackend.dto.SpotifyTrackResponseDto;
import com.progmatic.recordislandbackend.service.SpotifyService;
import com.wrapper.spotify.model_objects.specification.SavedAlbum;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author balza
 */
@RestController
public class SpotifyController {

    private final SpotifyService spotifyService;

    @Autowired
    public SpotifyController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping(path = "/api/spotify/authorizationCodeUri")
    public Map<String, Object> getAuthorizationCodeUri() {
        String uri = spotifyService.getAuthorizationCodeUriRequest().toString();

        Map<String, Object> response = new HashMap<>();
        response.put("uri", uri);
        return response;
    }

    @GetMapping(path = "/api/spotify/getAlbums")
    public List<SavedAlbum> getSavedAlbums(HttpSession session) throws Exception {
        return Arrays.asList(spotifyService.getSavedAlbums());
    }

    @GetMapping(path = "/api/spotify/getTracks")
    public List<SavedTrack> getSavedTracks(HttpSession session) throws Exception {
        return Arrays.asList(spotifyService.getSavedTracks());
    }

    @GetMapping(path = "/api/spotify/savedArtists")
    public HttpStatus saveSavedArtists() throws Exception {
        spotifyService.saveSpotifyArtistFromTracks(spotifyService.getSavedTracks());
        spotifyService.saveSpotifyArtistFromAlbums(spotifyService.getSavedAlbums());
        return HttpStatus.OK;
    }

    @GetMapping(path = "/api/spotify/callback")
    public void callback(@RequestParam String code) throws Exception {
        spotifyService.getToken(code);
    }
    
    @PutMapping(path = "/api/spotify/addAlbumToUser" )
    public HttpStatus addAlbumToUser(@RequestBody SpotifyAlbumDto album) throws Exception {
        spotifyService.saveAlbumsForCurrentUser(album.getArtist(), album.getAlbum());
        return HttpStatus.OK;
    }
    
    @GetMapping(path = "/api/spotify/getAlbumTracks")
    public Set<SpotifyTrackResponseDto> getAlbumTracks (SpotifyAlbumDto album) throws Exception {
        return spotifyService.getSpotifyAlbumTracks(album.getArtist(), album.getAlbum());
    }
}
