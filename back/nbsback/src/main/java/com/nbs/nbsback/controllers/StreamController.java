package com.nbs.nbsback.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nbs.nbsback.models.Stream;
import com.nbs.nbsback.services.StreamService;

@RestController
@RequestMapping("/streams")
public class StreamController {

    @Autowired
    private StreamService streamService;

    @GetMapping
    public List<Stream> getAllStreams(@RequestParam(required = false) Long activityId) {
        if (activityId != null) {
            return streamService.findStreamsByActivityId(activityId);
        }
        return streamService.getAllStreams();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stream> getStreamById(@PathVariable long id) {
        Optional<Stream> stream = streamService.getDataStreamById(id);
        return stream.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }   

}