package com.nbs.nbsback.clients;

import java.util.ArrayList;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.nbs.nbsback.stravamodels.StravaActivity;
import com.nbs.nbsback.stravamodels.StravaActivityDetail;
import com.nbs.nbsback.stravamodels.StravaAthlete;
import com.nbs.nbsback.stravamodels.StravaStream;

@Component
@FeignClient(name = "stravaApiClient", url = "${strava.api.base-url}")
public interface StravaApiClient {

        @GetMapping("/athlete")
        StravaAthlete getAthlete();

        @GetMapping("/athlete/activities")
        ArrayList<StravaActivity> getActivities(@RequestParam("before") Integer before,
                        @RequestParam("after") Integer after, @RequestParam("page") Integer page,
                        @RequestParam("per_page") Integer perPage);

        @GetMapping("/activities/{id}")
        StravaActivityDetail getActivity(@PathVariable("id") Long activityId,
                        @RequestParam("include_all_efforts") Boolean includeAllEfforts);

        @GetMapping("/activities/{id}/streams")
        ArrayList<StravaStream> getActivityStreams(@PathVariable("id") Long activityId,
                        @RequestParam("keys") String[] keys,
                        @RequestParam(value = "key_by_type", required = false) Boolean keyByType);

}