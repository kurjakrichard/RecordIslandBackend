package com.progmatic.recordislandbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("app.recordisland")
@Component
public class RecordIslandProperties {

    private String lastFmApiKey = "";
    private String discogsApiKey = "";
    private String discogssecretkey = "";

    public RecordIslandProperties() {
    }

    public String getLastFmApiKey() {
        return lastFmApiKey;
    }

    public String getDiscogsApiKey() {
        return discogsApiKey;
    }

    public String getDiscogssecretkey() {
        return discogssecretkey;
    }

    public void setLastFmApiKey(String lastFmApiKey) {
        this.lastFmApiKey = lastFmApiKey;
    }

    public void setDiscogsApiKey(String discogsApiKey) {
        this.discogsApiKey = discogsApiKey;
    }

    public void setDiscogssecretkey(String discogssecretkey) {
        this.discogssecretkey = discogssecretkey;
    }
}