package com.nbs.nbsback.clients;

import java.util.Map;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface StravaOauthClient { 

    @RequestLine("POST /token?client_id={clientId}&client_secret={clientSecret}&refresh_token={refreshToken}&grant_type={grantType}")
    @Headers({
            "Content-Type:application/x-www-form-urlencoded"
    })
    Map<String, Object> stravaToken(@Param("clientId") String clientId,
            @Param("clientSecret") String clientSecret,
            @Param("refreshToken") String refreshToken,
            @Param("grantType") String grantType);


}