package com.nbs.nbsback.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "stat", indexes = {
        @Index(name = "idx_type", columnList = "type"),
        @Index(name = "idx_start_date", columnList = "startDate"),
        @Index(name = "idx_end_date", columnList = "endDate"),
        @Index(name = "idx_type_start_date", columnList = "type, startDate"),
        @Index(name = "idx_athlete_id", columnList = "athleteId")
})
public class Stat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatType type;

    private Long athleteId;

    @Transient
    private List<Activity> activities;

    @JsonIgnore
    @Column(columnDefinition = "TEXT")
    private String activityIdsCsv;

    private double totalDistance; // in kilometers
    private double totalTime; // in hours
    private double averagePace; // in minutes per kilometer

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Stat() {}

    public Stat(StatType type, List<Activity> activities, LocalDateTime startDate, LocalDateTime endDate, Long athleteId) {
        this.type = type;
        this.activities = activities != null ? new ArrayList<>(activities) : new ArrayList<>();
        this.startDate = startDate;
        this.endDate = endDate;
        this.athleteId = athleteId;
        setActivityIds(this.activities.stream().map(Activity::getId).toList());
        calculateStats(this.activities);
    }

    public void calculateStats(List<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            this.totalDistance = 0;
            this.totalTime = 0;
            this.averagePace = 0;
            return;
        }

        this.totalDistance = activities.stream().mapToDouble(Activity::getDistance).sum() / 1000; // Convert to kilometers
        this.totalTime = activities.stream().mapToDouble(activity -> activity.getMovingTime() != null ? activity.getMovingTime() : 0).sum() / 3600; // Convert to hours
        this.averagePace = this.totalTime > 0 ? this.totalDistance / this.totalTime : 0;
    }

    public List<Long> getActivityIds() {
        if (activityIdsCsv == null || activityIdsCsv.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(activityIdsCsv.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(Long::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void setActivityIds(List<Long> activityIds) {
        List<Long> safeIds = activityIds != null ? activityIds : Collections.emptyList();
        this.activityIdsCsv = safeIds.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    public void addActivityId(Long activityId) {
        List<Long> activityIds = getActivityIds();
        if (!activityIds.contains(activityId)) {
            activityIds.add(activityId);
            setActivityIds(activityIds);
        }
    }

    
}