package com.nbs.nbsback.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbs.nbsback.services.StravaService;


@RestController
public class StravaController {

    @Autowired
    private StravaService stravaService;

    @GetMapping("/sync-athlete-data")
    public String syncAthleteData() {
        return stravaService.syncAthleteData();
    }

    @GetMapping("/sync-stats-data")
    public String syncStatsData() {
        return stravaService.syncAllStats();
    }
    
}