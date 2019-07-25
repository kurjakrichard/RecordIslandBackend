/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.domain.SpotifyAccessToken;
import com.progmatic.recordislandbackend.service.SpotifyService;
import com.wrapper.spotify.model_objects.specification.SavedAlbum;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Map<String, Object> getAuthorizationCodeUri(@RequestParam String code) {
        String uri = spotifyService.getAuthorizationCodeUriRequest().toString();
        
        Map<String, Object> response = new HashMap<>();
        response.put("uri", uri);
        return response;
    }
    
    @GetMapping(path = "/api/spotify/savedAlbums")
    public List<SavedAlbum> getSavedAlbums() throws Exception {
        return Arrays.asList(spotifyService.getSavedAlbums());
    }
    
    @GetMapping(path = "/api/spotify/savedTracks")
    public List<SavedTrack> getSavedTracks(HttpSession session) throws Exception {
        return Arrays.asList(spotifyService.getSavedTracks());
    }
    
    @GetMapping(path = "/api/spotify/callback")
    public void callback(@RequestParam String code) throws Exception {
        spotifyService.getToken(code);
    }
}
