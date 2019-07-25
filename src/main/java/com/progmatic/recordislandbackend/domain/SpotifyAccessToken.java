/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author balza
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SpotifyAccessToken implements Serializable { 
    private String token;
    private LocalDateTime expiresTime;
    private String refreshToken;
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(LocalDateTime expiresTime) {
        this.expiresTime = expiresTime;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public void setExpiration(int seconds) {
        expiresTime = LocalDateTime.now().plusSeconds(seconds).withSecond(0);
    }

    public boolean isExpired() {
        return expiresTime.isBefore(LocalDateTime.now());
    }
    
}
