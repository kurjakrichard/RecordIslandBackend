package com.progmatic.recordislandbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("app.recordisland")
@Component
public class RecordIslandProperties {

    private String lastFmApiKey = "";
    private String discogsApiKey = "";
    private String discogsSecretkey = "";
    private String spotifyClientId = "";
    private String spotifyClientSecret = "";
    private String frontend = "";
    private String ownEmail = "";
    private String spotifyRedirectUrl = "";

    public RecordIslandProperties() {
    }

    public String getLastFmApiKey() {
        return lastFmApiKey;
    }

    public String getDiscogsApiKey() {
        return discogsApiKey;
    }

    public String getDiscogsSecretkey() {
        return discogsSecretkey;
    }

    public String getSpotifyClientId() {
        return spotifyClientId;
    }

    public String getSpotifyClientSecret() {
        return spotifyClientSecret;
    }
    
    public void setLastFmApiKey(String lastFmApiKey) {
        this.lastFmApiKey = lastFmApiKey;
    }

    public void setDiscogsApiKey(String discogsApiKey) {
        this.discogsApiKey = discogsApiKey;
    }

    public void setDiscogsSecretkey(String discogsSecretkey) {
        this.discogsSecretkey = discogsSecretkey;
    }

    public void setSpotifyClientId(String spotifyClientId) {
        this.spotifyClientId = spotifyClientId;
    }

    public void setSpotifyClientSecret(String spotifyClientSecret) {
        this.spotifyClientSecret = spotifyClientSecret;
    }

    public String getFrontend() {
        return frontend;
    }

    public void setFrontend(String frontend) {
        this.frontend = frontend;
    }

    public String getOwnEmail() {
        return ownEmail;
    }

    public void setOwnEmail(String ownEmail) {
        this.ownEmail = ownEmail;
    }

    public String getSpotifyRedirectUrl() {
        return spotifyRedirectUrl;
    }

    public void setSpotifyRedirectUrl(String spotifyRedirectUrl) {
        this.spotifyRedirectUrl = spotifyRedirectUrl;
    }
    
}
