package com.nbs.nbsback.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nbs.nbsback.models.Stat;
import com.nbs.nbsback.services.StatsService;


@RestController
@RequestMapping("/stats")
public class StatsController {   
    
    @Autowired
    private StatsService statsService;
   
    @GetMapping("/{id}")
    public Stat getStatById(@PathVariable Long id) {
        return statsService.getStatById(id);
    }

    @GetMapping
    public List<Stat> getAllStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return statsService.getAllStats(date);
    }

}