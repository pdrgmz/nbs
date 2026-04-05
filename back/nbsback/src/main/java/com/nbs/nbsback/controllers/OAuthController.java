package com.nbs.nbsback.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OAuthController {

    @Value("${strava.client.id}")
    private String clientId;

    @Value("${strava.client.secret}")
    private String clientSecret;

    @Value("${strava.redirect.uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/auth/strava")
    public ResponseEntity<String> redirectToStrava() {
        String url = "https://www.strava.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=read,activity:read_all,profile:read_all";
        return ResponseEntity.status(302).header("Location", url).build();
    }

    @GetMapping("/auth/strava/exchange")
    public ResponseEntity<Map<String, Object>> exchangeToken(@RequestParam("code") String code) {
        String url = "https://www.strava.com/api/v3/oauth/token";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("code", code);
        requestBody.put("grant_type", "authorization_code");

        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);

        response.getHeaders().forEach((key, value) -> System.out.println(key + ": " + value));

        Map<String, Object> responseBody = response.getBody();
        return ResponseEntity.ok(responseBody);
    }
}