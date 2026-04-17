package com.nbs.nbsback.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nbs.nbsback.models.Athlete;
import com.nbs.nbsback.models.Stat;
import com.nbs.nbsback.models.StatType;


@Repository
public interface StatRepository extends JpaRepository<Stat, Long> {

    List<Stat> findByActivitiesId(Long activityId);

    @Query("SELECT s FROM Stat s WHERE :date BETWEEN s.startDate AND s.endDate")
    List<Stat> findStatsByDateInRange(@Param("date") LocalDateTime date);
    
    @Query("DELETE FROM Stat s WHERE s.athlete.id = :athleteId")
    void deleteByAthleteId(@Param("athleteId") Long athleteId);

    boolean existsByTypeAndAthleteAndStartDateAndEndDate(StatType type, Athlete athlete, LocalDateTime start,
            LocalDateTime end);

}