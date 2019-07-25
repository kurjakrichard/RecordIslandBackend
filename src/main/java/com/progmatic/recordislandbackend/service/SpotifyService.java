/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.config.RecordIslandProperties;
import com.progmatic.recordislandbackend.domain.SpotifyAccessToken;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
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
import org.springframework.context.annotation.Bean;

/**
 *
 * @author balza
 */
@Service
public class SpotifyService {

    private final RecordIslandProperties properties;
    private SpotifyAccessToken spotifyAccestoken;
    private URI spotifyRedirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/api/spotify/callback");

    @Autowired
    public SpotifyService(RecordIslandProperties properties, SpotifyAccessToken spotifyAccestoken) {
        this.properties = properties;
        this.spotifyAccestoken = spotifyAccestoken;
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
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken("TOKEN")
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
}
