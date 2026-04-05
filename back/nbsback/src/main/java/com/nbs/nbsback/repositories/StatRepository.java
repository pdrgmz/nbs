package com.nbs.nbsback.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nbs.nbsback.models.Stat;

@Repository
public interface StatRepository extends JpaRepository<Stat, Long> {

    List<Stat> findByActivitiesId(Long activityId);

}