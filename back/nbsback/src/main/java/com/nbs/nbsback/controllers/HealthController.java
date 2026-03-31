package com.nbs.nbsback.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Value("${application.version:unknown}")
    private String appVersion;

    @GetMapping
    public String healthCheck() {
        return "OK - Version: " + appVersion;
    }
}