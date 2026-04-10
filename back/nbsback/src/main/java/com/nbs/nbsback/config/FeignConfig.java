package com.nbs.nbsback.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nbs.nbsback.services.TokenService;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

@Configuration
public class FeignConfig {

    @Autowired
    private TokenService tokenService;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Retrieve the Authorization token from TokenService
            String accessToken = tokenService.getAccesToken();
            
            if (accessToken != null) {
                requestTemplate.header("Authorization", "Bearer " + accessToken);
            } else {
                System.err.println("Access token is null. Ensure the token is refreshed or available.");
            }
        };
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

   
}