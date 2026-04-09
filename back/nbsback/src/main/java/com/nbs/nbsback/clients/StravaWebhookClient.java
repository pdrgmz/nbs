package com.nbs.nbsback.clients;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "stravaWebhookClient", url = "https://www.strava.com/api/v3")
public interface StravaWebhookClient {

    @PostMapping(value = "/push_subscriptions", consumes = "application/x-www-form-urlencoded")
    Map<String, Object> subscribeToWebhook(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("callback_url") String callbackUrl,
            @RequestParam("verify_token") String verifyToken
    );
}