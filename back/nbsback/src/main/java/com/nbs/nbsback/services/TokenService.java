package com.nbs.nbsback.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import com.nbs.nbsback.clients.StravaOauthClient;
import com.nbs.nbsback.models.Athlete;
import com.nbs.nbsback.repositories.AthleteRepository;

@Service
public class TokenService {

    @Lazy
    @Autowired
    private AthleteRepository athleteRepository;

    @Lazy
    @Autowired
    private StravaOauthClient stravaOauthClient;

    @Value("${strava.client.id}")
    private String clientId;

    @Value("${strava.client.secret}")
    private String clientSecret;

    private static final ThreadLocal<String> accessTokenHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> refreshTokenHolder = new ThreadLocal<>();

    public void setAccessToken(String token) {
        accessTokenHolder.set(token);
    }

    public String getAccessToken() {
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

    public void refreshToken() {
        Map<String, Object> response = stravaOauthClient.stravaToken(
                clientId,
                clientSecret,
                getRefreshToken(), "refresh_token");
        String newAccessToken = (String) response.get("access_token");
        setAccessToken(newAccessToken);
    }

    public String refreshToken(Long athleteId) {
        
        Athlete athlete = athleteRepository.findById(athleteId).orElseThrow(() -> new IllegalArgumentException("Athlete not found"));
        String refreshToken = athlete.getRefreshToken();

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalStateException("Refresh token is missing for athlete: " + athleteId);
        }

        // Call StravaOauthClient to exchange refresh token for a new access token
        Map<String, Object> response = stravaOauthClient.stravaToken(clientId, clientSecret, refreshToken, "refresh_token");
        String newAccessToken = (String) response.get("access_token");
        String newRefreshToken = (String) response.get("refresh_token");

        // Update athlete with new tokens
        athlete.setAccessToken(newAccessToken);
        athlete.setRefreshToken(newRefreshToken);
        athleteRepository.save(athlete);

        setAccessToken(newAccessToken);
        setRefreshToken(newRefreshToken);

        return newAccessToken;
    }

}