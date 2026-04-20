package com.nbs.nbsback.repositories;

import com.nbs.nbsback.models.Stream;
import com.nbs.nbsback.models.StreamId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StreamRepository extends JpaRepository<Stream, StreamId> {
    List<Stream> findByActivityId(Long activityId);
}