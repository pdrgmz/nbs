package com.nbs.nbsback.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nbs.nbsback.interceptors.TokenValidationInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {   

    @Autowired
    private TokenValidationInterceptor tokenValidationInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://nbs.pdrgmz.uk", "http://nbsb.pdrgmz.uk", "http://localhost:3000", "https://nbs.pdrgmz.uk")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenValidationInterceptor)
                .excludePathPatterns("/webhook/**", "/sync-data/**", "/swagger-ui/**", "/v3/api-docs/**"); // Exclude specific endpoints
    }
}