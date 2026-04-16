package com.nbs.nbsback.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nbs.nbsback.services.StravaService;


@RestController
public class StravaController {

    @Autowired
    private StravaService stravaService;

    @GetMapping("/sync-data")
    public String syncAthleteData(@RequestParam("athlete_id") Long athleteId) {
        return stravaService.syncAthleteData(athleteId);
    }
   
}