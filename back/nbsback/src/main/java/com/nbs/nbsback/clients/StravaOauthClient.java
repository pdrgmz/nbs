package com.nbs.nbsback.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "stravaOauthClient", url = "https://www.strava.com/api/v3/oauth")
public interface StravaOauthClient {   

    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    Map<String, Object> exchangeToken(@RequestParam("client_id") String clientId,
                                       @RequestParam("client_secret") String clientSecret,
                                       @RequestParam("refresh_token") String refreshToken,
                                       @RequestParam("grant_type") String grantType);

    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    Map<String, Object> refreshToken(@RequestParam("client_id") String clientId,
                                      @RequestParam("client_secret") String clientSecret,
                                      @RequestParam("grant_type") String grantType,
                                      @RequestParam("refresh_token") String refreshToken);


}