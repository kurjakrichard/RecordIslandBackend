package com.progmatic.recordislandbackend.controller;

import com.progmatic.recordislandbackend.dto.SpotifyAlbumDto;
import com.progmatic.recordislandbackend.dto.SpotifyPlaylistAndAlbumDto;
import com.progmatic.recordislandbackend.dto.SpotifyPlaylistDto;
import com.progmatic.recordislandbackend.dto.SpotifyPlaylistResponseDto;
import com.progmatic.recordislandbackend.dto.SpotifyTrackResponseDto;
import com.progmatic.recordislandbackend.exception.AlbumNotExistsException;
import com.progmatic.recordislandbackend.service.SpotifyService;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.SavedAlbum;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping(path = "/api/getSpotifyHistory")
    public void saveSavedArtists() throws Exception {
//        spotifyService.saveSpotifyArtistFromTracks(spotifyService.getSavedTracks());
        spotifyService.saveSpotifyArtistFromAlbums(spotifyService.getSavedAlbums());
    }

    @GetMapping(path = "/api/spotify/callback")
    public void callback(@RequestParam String code) throws Exception {
        spotifyService.getToken(code);
    }
    
    @PutMapping(path = "/api/spotify/addAlbumToUser")
    public void addAlbumToUser(@RequestBody SpotifyAlbumDto album) throws Exception {
        spotifyService.saveAlbumsForCurrentUser(album.getArtist(), album.getAlbum());
    }
    
    @GetMapping(path = "/api/spotify/getAlbumTracks")
    public Set<SpotifyTrackResponseDto> getAlbumTracks (SpotifyAlbumDto album) throws Exception {
        return spotifyService.getSpotifyAlbumTracks(album.getArtist(), album.getAlbum());
    }
    
    @GetMapping(path = "/api/spotify/getUserPlaylists")
    public List<SpotifyPlaylistResponseDto> getUserPlaylists() throws SpotifyWebApiException, IOException {
        return spotifyService.getListOfCurrentUsersPlaylists();
    }
    
    @PostMapping(path = "/api/spotify/addAlbumToPlaylist")
    public void addAlbumToPlaylist(@RequestBody SpotifyPlaylistAndAlbumDto playlist) throws IOException, SpotifyWebApiException, AlbumNotExistsException {
        System.out.println("/api/spotify/addAlbumToPlaylist");
        SpotifyPlaylistDto playlistDto = new SpotifyPlaylistDto();
        playlistDto.setId(playlist.getPlaylistId());
        spotifyService.addTracksToSpotifyPlaylist(playlistDto, playlist.getArtist(), playlist.getAlbum());
    }
}
