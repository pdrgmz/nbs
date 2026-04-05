package com.nbs.nbsback.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbs.nbsback.models.Activity;
import com.nbs.nbsback.models.Stat;
import com.nbs.nbsback.models.Stream;
import com.nbs.nbsback.services.ActivityService;
import com.nbs.nbsback.services.StatsService;
import com.nbs.nbsback.services.StreamService;


@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;    

    @Autowired
    private StreamService streamService;

    @Autowired
    private StatsService statsService;

    @GetMapping
    public List<Activity> getAllActivities() {
        return activityService.getAllActivities();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable long id) {
        Optional<Activity> activity = activityService.getActivityById(id);
        return activity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/streams")
    public ResponseEntity<List<Stream>> getActivityStreamsById(@PathVariable long id) {
        List<Stream> streams = streamService.findStreamsByActivityId(id);
        if (streams.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(streams);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<List<Stat>> getActivityStatsById(@PathVariable long id) {
         List<Stat> stats = statsService.getStatsByActivityId(id);
        if (stats.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

        

    

}