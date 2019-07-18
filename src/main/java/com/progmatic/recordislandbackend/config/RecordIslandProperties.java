package com.progmatic.recordislandbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("app.recordisland")
@Component
public class RecordIslandProperties {
    
    private String lastFmApiKey = "";

    public RecordIslandProperties() {
    }

    public String getLastFmApiKey() {
        return lastFmApiKey;
    }

    public void setLastFmApiKey(String lastFmApiKey) {
        this.lastFmApiKey = lastFmApiKey;
    }
    
}
