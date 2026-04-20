package com.nbs.nbsback.services;

 
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nbs.nbsback.models.Activity;
import com.nbs.nbsback.repositories.ActivityRepository;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;


    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    public ArrayList<Activity> getActivitiesByDateRange(LocalDateTime startDate, LocalDateTime endDate, String type) {
        return activityRepository.findByStartDateBetweenAndType(startDate, endDate, type);
    }
       
}