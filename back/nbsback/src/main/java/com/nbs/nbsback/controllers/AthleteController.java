package com.nbs.nbsback.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbs.nbsback.models.Athlete;
import com.nbs.nbsback.services.AthleteService;

@RestController
@RequestMapping("/athletes")
public class AthleteController {

    @Autowired
    private AthleteService athleteService;

    @GetMapping("/{id}")
    public ResponseEntity<Athlete> getAthleteById(@PathVariable long id) {
        Optional<Athlete> athlete = athleteService.getAthleteById(id);
        return athlete.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}