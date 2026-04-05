package com.nbs.nbsback.models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
public class Stat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatType type;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "athlete_id", nullable = false)
    private Athlete athlete;

    @ManyToMany
    @JoinTable(
        name = "stat_activities",
        joinColumns = @JoinColumn(name = "stat_id"),
        inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    private List<Activity> activities;

    private double totalDistance; // in kilometers
    private double totalTime; // in hours
    private double averagePace; // in minutes per kilometer

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Stat() {}

    public Stat(StatType type, List<Activity> activities, LocalDateTime startDate, LocalDateTime endDate, Athlete athlete) {
        this.type = type;
        this.activities = activities;
        this.startDate = startDate;
        this.endDate = endDate;
        this.athlete = athlete;
        calculateStats(activities);
    }

      private void calculateStats(List<Activity> activities) {
        this.totalDistance = activities.stream().mapToDouble(Activity::getDistance).sum() / 1000; // Convert to kilometers
        this.totalTime = activities.stream().mapToDouble(Activity::getMovingTime).sum() / 3600; // Convert to hours
        this.averagePace = this.totalTime > 0 ? this.totalDistance / this.totalTime : 0;
    }

    
}