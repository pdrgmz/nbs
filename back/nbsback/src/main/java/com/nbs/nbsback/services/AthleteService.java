package com.nbs.nbsback.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nbs.nbsback.models.Athlete;
import com.nbs.nbsback.repositories.AthleteRepository;

@Service
public class AthleteService {

    @Autowired
    private AthleteRepository athleteRepository;
   
    public Optional<Athlete> getAthleteById(Long id) {
        return athleteRepository.findById(id);
    }

  
}