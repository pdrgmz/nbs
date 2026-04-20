package com.nbs.nbsback.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nbs.nbsback.models.Stream;
import com.nbs.nbsback.models.StreamId;
import com.nbs.nbsback.repositories.StreamRepository;

@Service
public class StreamService {

    @Autowired
    private StreamRepository streamRepository;

    public Optional<Stream> getDataStreamById(Long activityId, String type) {
        return streamRepository.findById(new StreamId(activityId, type));
    }

    public List<Stream> getAllStreams() {
        return streamRepository.findAll();
    }

    public List<Stream> findStreamsByActivityId(Long activityId) {
        return streamRepository.findByActivityId(activityId);
    }

}