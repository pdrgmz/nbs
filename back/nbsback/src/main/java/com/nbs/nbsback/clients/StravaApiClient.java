package com.nbs.nbsback.clients;

import java.util.ArrayList;

import com.nbs.nbsback.stravamodels.StravaActivity;
import com.nbs.nbsback.stravamodels.StravaActivityDetail;
import com.nbs.nbsback.stravamodels.StravaAthlete;
import com.nbs.nbsback.stravamodels.StravaStream;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface StravaApiClient {

        @RequestLine("GET /athlete")
        @Headers({
                        "Content-Type: application/json",
                        "Authorization: Bearer {token}"
        })
        StravaAthlete getAthlete(@Param("token") String token);


        @RequestLine("GET /athlete/activities?before={before}&after={after}&page={page}&per_page={perPage}")
        @Headers({
                        "Content-Type: application/json",
                        "Authorization: Bearer {token}"
        })
        ArrayList<StravaActivity> getActivities(@Param("token") String token, @Param("before") Integer before,
                        @Param("after") Integer after, @Param("page") Integer page,
                        @Param("perPage") Integer perPage);


        @RequestLine("GET /activities/{id}?include_all_efforts={includeAllEfforts}")
        @Headers({
                        "Content-Type: application/json",
                        "Authorization: Bearer {token}"
        })
        StravaActivityDetail getActivity(@Param("token") String token, @Param("id") Long activityId,
                        @Param("includeAllEfforts") Boolean includeAllEfforts);


        @RequestLine("GET /activities/{id}/streams?keys={keys}&key_by_type={keyByType}")
        @Headers({
                        "Content-Type: application/json",
                        "Authorization: Bearer {token}"
        })
        ArrayList<StravaStream> getActivityStreams(@Param("token") String token, @Param("id") Long activityId,
                        @Param("keys") String keys,
                        @Param("keyByType") Boolean keyByType);

}