package com.nbs.nbsback.clients;

import java.util.Map;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface StravaWebhookClient {

    @RequestLine("POST /push_subscriptions?client_id={clientId}&client_secret={clientSecret}&callback_url={callbackUrl}&verify_token={verifyToken}")
    @Headers({
            "Content-Type:application/x-www-form-urlencoded"
    })
    Map<String, Object> subscribeToWebhook(
            @Param("clientId") String clientId,
            @Param("clientSecret") String clientSecret,
            @Param("callbackUrl") String callbackUrl,
            @Param("verifyToken") String verifyToken
    );
}