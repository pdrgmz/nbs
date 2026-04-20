package com.nbs.nbsback.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nbs.nbsback.models.Stat;
import com.nbs.nbsback.models.StatType;


@Repository
public interface StatRepository extends JpaRepository<Stat, Long> {

    @Query("SELECT s FROM Stat s WHERE :date BETWEEN s.startDate AND s.endDate")
    List<Stat> findStatsByDateInRange(@Param("date") LocalDateTime date);

    @Query("SELECT s FROM Stat s WHERE :date BETWEEN s.startDate AND s.endDate AND s.athleteId = :athleteId")
    List<Stat> findStatsByDateInRangeAndAthleteId(@Param("date") LocalDateTime date, @Param("athleteId") Long athleteId);
    
    @Query("DELETE FROM Stat s WHERE s.athleteId = :athleteId")
    void deleteByAthleteId(@Param("athleteId") Long athleteId);

    boolean existsByTypeAndAthleteIdAndStartDateAndEndDate(StatType type, Long athleteId, LocalDateTime start,
            LocalDateTime end);

}