package com.nbs.nbsback.repositories;

import com.nbs.nbsback.models.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {
    List<Stream> findByActivityId(Long activityId);
}