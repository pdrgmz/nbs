package com.nbs.nbsback.repositories;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nbs.nbsback.models.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    ArrayList<Activity> findByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    ArrayList<Activity> findByStartDateBetweenAndType(LocalDateTime startDate, LocalDateTime endDate, String type);

    List<Activity> findByStartDateBetweenAndAthleteIdAndType(LocalDateTime startOfYear, LocalDateTime endOfYear,
            Long athleteId, String string);

}