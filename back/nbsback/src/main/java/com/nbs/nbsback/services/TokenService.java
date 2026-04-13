package com.nbs.nbsback.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nbs.nbsback.clients.StravaOauthClient;

@Service
public class TokenService {

    @Value("${strava.client.id}")
    private String clientId;

    @Value("${strava.client.secret}")
    private String clientSecret;

    @Autowired
    private StravaOauthClient stravaOauthClient;

    private static final ThreadLocal<String> accessTokenHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> refreshTokenHolder = new ThreadLocal<>();

 

    public void setAccesToken(String token) {
        accessTokenHolder.set(token);
    }

    public String getAccesToken() {
        return accessTokenHolder.get();
    }

    public void setRefreshToken(String token) {
        refreshTokenHolder.set(token);
    }

    public String getRefreshToken() {
        return refreshTokenHolder.get();
    }

    public void clearAccessToken() {
        accessTokenHolder.remove();
    }

    public void clearRefreshToken() {
        refreshTokenHolder.remove();
    }

    public void exchangeToken(String refreshToken) {
        System.out.println("---------------------------------------------------------");
        
        Map<String,Object> response = stravaOauthClient.exchangeToken(
            clientId,
            clientSecret,
            refreshToken, "refresh_token");

        String newAccessToken = (String) response.get("access_token");
        setAccesToken(newAccessToken);
    }

}