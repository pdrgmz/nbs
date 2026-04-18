package com.nbs.nbsback.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nbs.nbsback.clients.StravaApiClient;
import com.nbs.nbsback.clients.StravaOauthClient;
import com.nbs.nbsback.clients.StravaWebhookClient;
import com.nbs.nbsback.services.TokenService;

import feign.Feign;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.optionals.OptionalDecoder;

@Configuration
public class FeignConfig {

    @Value("${strava.api.base-url}")
    private String stravaApiBaseUrl;

    @Value("${strava.oauth.base-url}")
    private String stravaOauthBaseUrl;

    private final TokenService tokenService;

    public FeignConfig(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // Log full request and response details
    }

    @Bean
    public ObjectMapper customObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public Decoder feignDecoder(ObjectMapper customObjectMapper) {
        return new JacksonDecoder(customObjectMapper);
    }

    @Bean
    public Encoder feignEncoder(ObjectMapper customObjectMapper) {
        return new JacksonEncoder(customObjectMapper);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Exclude StravaOauthClient from using this interceptor
                if (template.url().contains("/oauth")) {
                    return;
                }

                String accessToken = tokenService.getAccessToken();
                if (accessToken == null || accessToken.isEmpty()) {
                    tokenService.refreshToken();
                    accessToken = tokenService.getAccessToken();
                }
                if (accessToken != null && !accessToken.isEmpty()) {
                    template.header("Authorization", "Bearer " + accessToken);
                }
            }
        };
    }

    @Bean
    public StravaApiClient iStravaApiClient() {
        return builderClient(
                stravaApiBaseUrl,
                "FULL",
                new Retryer.Default(),
                new Logger.JavaLogger()).target(StravaApiClient.class, stravaApiBaseUrl);
    }

    @Bean
    public StravaOauthClient iStravaOauthClient() {
        return builderClient(
                stravaOauthBaseUrl,
                "FULL",
                new Retryer.Default(),
                new Logger.JavaLogger()).target(StravaOauthClient.class, stravaOauthBaseUrl);
    }

    @Bean
    public StravaWebhookClient iStravaWebhookClient() {
        return builderClient(
                stravaApiBaseUrl,
                "FULL",
                new Retryer.Default(),
                new Logger.JavaLogger()).target(StravaWebhookClient.class, stravaApiBaseUrl);
    }

    @Bean
    public Retryer customRetryer() {
        return new Retryer.Default() {
            @Override
            public void continueOrPropagate(RetryableException e) {
                if (e.status() == 429) { // Check status directly
                    try {
                        Thread.sleep(15 * 60 * 1000); // Wait for 15 minutes
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread interrupted during retry wait", interruptedException);
                    }
                } else {
                    super.continueOrPropagate(e);
                }
            }
        };
    }

    private Feign.Builder builderClient(String apiEndpoint, String logLevel, Retryer retryer,
            Logger logger) {
        ObjectMapper customObjectMapper = customObjectMapper();
        return Feign
                .builder()
                .encoder(new JacksonEncoder(customObjectMapper))
                .decoder(new OptionalDecoder(new ResponseEntityDecoder(new JacksonDecoder(customObjectMapper))))
                .logger(logger)
                .logLevel(Logger.Level.valueOf(logLevel))
                .retryer(retryer);
        // .errorDecoder(feignErrorDecoder);
        // .requestInterceptor(new TokenValidationInterceptor());
    }

}