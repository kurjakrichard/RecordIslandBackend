/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.config.RecordIslandProperties;
import com.progmatic.recordislandbackend.dao.ArtistRepository;
import com.progmatic.recordislandbackend.dao.UserRepository;
import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.domain.SpotifyAccessToken;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.exception.SpotifyTokenNotFoundExcepion;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import java.net.URI;
import com.wrapper.spotify.model_objects.specification.SavedAlbum;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.data.library.GetCurrentUsersSavedAlbumsRequest;
import com.wrapper.spotify.requests.data.library.GetUsersSavedTracksRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import java.io.IOException;
import java.util.NoSuchElementException;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author balza
 */
@Service
public class SpotifyService {

    private final RecordIslandProperties properties;
    private SpotifyAccessToken spotifyAccestoken;
    private URI spotifyRedirectUri = SpotifyHttpManager.makeUri("http://localhost:4200/api/spotify/callback");
    private final UserService userService;
    private ArtistRepository artistRepository;
    private UserRepository userRepository;

//    @PersistenceContext
//    private EntityManager em;
    @Autowired
    public SpotifyService(RecordIslandProperties properties, SpotifyAccessToken spotifyAccestoken, UserService userService, ArtistRepository artistRepository, UserRepository userRepository) {
        this.properties = properties;
        this.spotifyAccestoken = spotifyAccestoken;
        this.userService = userService;
        this.artistRepository = artistRepository;
        this.userRepository = userRepository;
    }

    public URI getAuthorizationCodeUriRequest() {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(properties.getSpotifyClientId())
                .setClientSecret(properties.getSpotifyClientSecret())
                .setRedirectUri(spotifyRedirectUri)
                .build();
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope("user-library-read")
                //          .state("x4xkmn9pu3j6ukrs8n")
                //          .scope("user-read-birthdate,user-read-email")
                //          .show_dialog(true)
                .build();
        final URI uri = authorizationCodeUriRequest.execute();
        return uri;
    }

    public void getToken(String code) throws SpotifyWebApiException, IOException {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(properties.getSpotifyClientId())
                .setClientSecret(properties.getSpotifyClientSecret())
                .setRedirectUri(spotifyRedirectUri)
                .build();
        final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();

        final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

        System.out.println(authorizationCodeCredentials.getExpiresIn());
        spotifyAccestoken.setToken(authorizationCodeCredentials.getAccessToken());
        spotifyAccestoken.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
        spotifyAccestoken.setExpiration(authorizationCodeCredentials.getExpiresIn());

    }

    public void refreshToken(String refreshToken) throws SpotifyWebApiException, IOException {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(properties.getSpotifyClientId())
                .setClientSecret(properties.getSpotifyClientSecret())
                .setRefreshToken(refreshToken)
                .build();
        final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
                .build();
        final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

        spotifyAccestoken.setToken(authorizationCodeCredentials.getAccessToken());
        spotifyAccestoken.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
        spotifyAccestoken.setExpiration(authorizationCodeCredentials.getExpiresIn());
    }

    public SavedAlbum[] getSavedAlbums() throws SpotifyWebApiException, IOException {
        if (spotifyAccestoken.isExpired()) {
            refreshToken(spotifyAccestoken.getRefreshToken());
        }
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(spotifyAccestoken.getToken())
                .build();
        final GetCurrentUsersSavedAlbumsRequest getCurrentUsersSavedAlbumsRequest = spotifyApi
                .getCurrentUsersSavedAlbums()
                //          .limit(10)
                //          .market(CountryCode.SE)
                //          .offset(0)
                .build();
        SavedAlbum[] savedAlbums = getCurrentUsersSavedAlbumsRequest.execute().getItems();
        return savedAlbums;
    }

    public SavedTrack[] getSavedTracks() throws SpotifyWebApiException, IOException {
        
        if(spotifyAccestoken.getToken() == null) {
            throw new SpotifyTokenNotFoundExcepion("Token not found!");
        }

        if (spotifyAccestoken.isExpired()) {
            refreshToken(spotifyAccestoken.getRefreshToken());
        }

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(spotifyAccestoken.getToken())
                .build();
        final GetUsersSavedTracksRequest getUsersSavedTracksRequest = spotifyApi.getUsersSavedTracks()
                //          .limit(10)
                //          .market(CountryCode.SE)
                //          .offset(0)
                .build();
        SavedTrack[] savedTracks = getUsersSavedTracksRequest.execute().getItems();
        return savedTracks;
    }

    @Transactional
    public void saveSpotifyArtistFromTracks(SavedTrack[] tracks) throws SpotifyWebApiException, IOException {
        for (SavedTrack track : tracks) {
            ArtistSimplified[] artists = track.getTrack().getArtists();
            Artist artist;
            for (ArtistSimplified artistSname : artists) {
                String artistName = artistSname.getName();              
                System.out.println("Artist from track: " + artistName);
                try {
                    artist = artistRepository.findByName(artistName).get();
                } catch (NoSuchElementException e) {
                    artist = new Artist(artistName);
                    artistRepository.save(artist);
                    String username = SecurityContextHolder.getContext().getAuthentication().getName();
                    User user = (User) userService.loadUserByUsername(username);
                    user.getLikedArtists().add(artist);
                    userRepository.save(user);
                    continue;
                }
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = (User) userService.loadUserByUsername(username);
                user.getLikedArtists().add(artist);
                userRepository.save(user);
            }
        }
    }

    @Transactional
    public void saveSpotifyArtistFromAlbums(SavedAlbum[] savedAlbums) {
        System.out.println("A kedvenc albumok száma: " + savedAlbums.length);
        for (SavedAlbum savedAlbum : savedAlbums) {
            ArtistSimplified[] artists = savedAlbum.getAlbum().getArtists();
            Artist artist;
            for (ArtistSimplified artistSname : artists) {
                String artistName = artistSname.getName();
                System.out.println("Artist from album: " + artistName);
                try {
                    artist = artistRepository.findByName(artistName).get();
                } catch (NoSuchElementException e) {
                    artist = new Artist(artistName);
                    artistRepository.save(artist);
                    String username = SecurityContextHolder.getContext().getAuthentication().getName();
                    User user = (User) userService.loadUserByUsername(username);
                    user.getLikedArtists().add(artist);
                    userRepository.save(user);
                }
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = (User) userService.loadUserByUsername(username);
                user.getLikedArtists().add(artist);
                userRepository.save(user);
            }
        }
    }
}
