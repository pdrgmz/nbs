package com.nbs.nbsback.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Response;
import feign.codec.ErrorDecoder;

@Configuration
public class FeignErrorDecoder implements ErrorDecoder {

    private static final Logger logger = LoggerFactory.getLogger(FeignErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            logger.error("404 Not Found: {} - {}", methodKey, response.request().url());
            return new RuntimeException("Resource not found: " + response.request().url());
        }
        // Default behavior for other errors
        return new Default().decode(methodKey, response);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}