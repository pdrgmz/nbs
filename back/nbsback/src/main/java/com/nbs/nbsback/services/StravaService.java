package com.nbs.nbsback.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbs.nbsback.clients.StravaApiClient;
import com.nbs.nbsback.models.Activity;
import com.nbs.nbsback.models.Athlete;
import com.nbs.nbsback.models.Stream;
import com.nbs.nbsback.repositories.ActivityRepository;
import com.nbs.nbsback.repositories.AthleteRepository;
import com.nbs.nbsback.repositories.StreamRepository;
import com.nbs.nbsback.stravamodels.StravaActivity;
import com.nbs.nbsback.stravamodels.StravaActivityDetail;
import com.nbs.nbsback.stravamodels.StravaAthlete;
import com.nbs.nbsback.stravamodels.StravaStream;

@Service
public class StravaService {

    private static final Logger logger = LoggerFactory.getLogger(StravaService.class);

    @Autowired
    private StravaApiClient stravaApiClient;

    @Autowired
    private AthleteRepository athleteRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private StreamRepository streamRepository;

    @Autowired
    private StatsService statsService;

    public String syncAthleteData() {

        StravaAthlete stravaAthlete = fetchAndSaveAthleteData();

        if (stravaAthlete == null) {
            return "Failed to synchronize athlete data. Check logs for details.";
        }

        Athlete athlete = athleteRepository.findById(stravaAthlete.getId())
                .orElseThrow(() -> new IllegalArgumentException("Athlete not found with ID: " + stravaAthlete.getId()));

        LocalDateTime now = LocalDateTime.now();
        for (int year = athlete.getCreatedAt().getYear(); year <= now.getYear(); year++) {
            try {
                statsService.calculateAllStats(year, athlete);
            } catch (Exception e) {
                logger.error("Error calculating stats for year {}", year);
            }
        }

        return "Data synchronization done. Check logs for details.";

    }

    public String syncAllStats() {

        Athlete athlete = athleteRepository.findById(60270508L)
                .orElseThrow(() -> new IllegalArgumentException("Athlete not found with ID: " + 60270508L));

        LocalDateTime now = LocalDateTime.now();
        for (int year = athlete.getCreatedAt().getYear(); year <= now.getYear(); year++) {
            try {
                statsService.calculateAllStats(year, athlete);
            } catch (Exception e) {
                logger.error("Error calculating stats for year {}", year);
            }
        }

        return "Data synchronization done. Check logs for details.";
    }

    private StravaAthlete fetchAndSaveAthleteData() {
        logger.info("Starting athlete data synchronization...");
        try {

            logger.info("Fetching athlete data from Strava API...");
            StravaAthlete stravaAthlete = stravaApiClient.getAthlete();

            logger.info("Building and saving athlete data...");
            Athlete athlete = buildAthleteFromStrava(stravaAthlete);
            athleteRepository.save(athlete);

            logger.info("Fetching activities from Strava API...");
            List<StravaActivity> stravaActivities = stravaApiClient.getActivities(null, null, 1, 150);

            for (StravaActivity stravaActivity : stravaActivities) {
                buildStravaactivityFull(athlete, stravaActivity.getId());
            }

            logger.info("Athlete data synchronization completed successfully.");
            return stravaAthlete;
        } catch (Exception e) {
            logger.error("Error during synchronization: {}", e.getMessage(), e);
            return null;
        }
    }

    private void buildStravaactivityFull(Athlete athlete, Long activityId) {
        logger.info("Fetching details for activity ID: {}", activityId);
        StravaActivityDetail stravaActivityDetail = stravaApiClient.getActivity(activityId, true);

        logger.info("Building and saving activity data for activity ID: {}", activityId);
        Activity activity = buildActivityFromStrava(athlete, stravaActivityDetail);
        activityRepository.save(activity);

        String[] keys = {
                "time",
                "latlng",
                "distance",
                "altitude",
                "velocity_smooth",
                "heartrate",
                "cadence",
                "watts",
                "temp",
                "moving",
                "grade_smooth"
        };

        logger.info("Fetching streams for activity ID: {}", activityId);
        ArrayList<StravaStream> stravaStreams = stravaApiClient.getActivityStreams(activityId,
                keys, null);

        for (StravaStream stravaStream : stravaStreams) {
            logger.info("Building and saving stream data of type: {} for activity ID: {}",
                    stravaStream.getType(), activityId);

            Stream stream = buildDataStreamFromStrava(activity, stravaStream);
            streamRepository.save(stream);
        }
    }

    private Athlete buildAthleteFromStrava(StravaAthlete stravaAthlete) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        return new Athlete().builder()
                .id(stravaAthlete.getId())

                .username(stravaAthlete.getUsername())
                .firstname(stravaAthlete.getFirstname())

                .lastname(stravaAthlete.getLastname())
                .city(stravaAthlete.getCity())
                .state(stravaAthlete.getState())
                .country(stravaAthlete.getCountry())
                .sex(stravaAthlete.getSex())

                .createdAt(LocalDateTime.parse(stravaAthlete.getCreatedAt(), formatter))
                .updatedAt(LocalDateTime.parse(stravaAthlete.getUpdatedAt(), formatter))

                .profileMedium(stravaAthlete.getProfileMedium())
                .profile(stravaAthlete.getProfile())
                .build();
    }

    private Activity buildActivityFromStrava(Athlete athlete, StravaActivityDetail stravaActivityDetail) {
        return new Activity().builder()
                .id(stravaActivityDetail.getId())

                .athlete(athlete) // Assign the Athlete object instead of athleteId

                .name(stravaActivityDetail.getName())
                .distance(stravaActivityDetail.getDistance())
                .movingTime(stravaActivityDetail.getMovingTime())
                .elapsedTime(stravaActivityDetail.getElapsedTime())
                .totalElevationGain(stravaActivityDetail.getTotalElevationGain())
                .type(stravaActivityDetail.getType())
                .sportType(stravaActivityDetail.getSportType())
                .deviceName(stravaActivityDetail.getDeviceName())
                .startDate(stravaActivityDetail.getStartDate())
                .startDateLocal(stravaActivityDetail.getStartDateLocal())
                .timezone(stravaActivityDetail.getTimezone())
                .utcOffset(stravaActivityDetail.getUtcOffset())
                .startLatlng(stravaActivityDetail.getStartLatlng())
                .endLatlng(stravaActivityDetail.getEndLatlng())
                .averageSpeed(stravaActivityDetail.getAverageSpeed())
                .maxSpeed(stravaActivityDetail.getMaxSpeed())
                .averageCadence(stravaActivityDetail.getAverageCadence())
                .hasHeartrate(stravaActivityDetail.isHasHeartrate())
                .averageHeartrate(stravaActivityDetail.getAverageHeartrate())
                .maxHeartrate(stravaActivityDetail.getMaxHeartrate())
                .elevHigh(stravaActivityDetail.getElevHigh())
                .elevLow(stravaActivityDetail.getElevLow())

                .polyline(stravaActivityDetail.getMap().getPolyline())
                .summaryPolyline(stravaActivityDetail.getMap().getSummaryPolyline())

                .polylinePoints(decodePolyline(stravaActivityDetail.getMap().getPolyline()))
                .summaryPolylinePoints(decodePolyline(stravaActivityDetail.getMap().getSummaryPolyline()))

                .build();
    }

    private Stream buildDataStreamFromStrava(Activity activity, StravaStream stravaStream) {

        ObjectMapper objectMapper = new ObjectMapper();
        String dataJson = null;
        try {
            dataJson = objectMapper.writeValueAsString(stravaStream.getData());
        } catch (JsonProcessingException e) {
            logger.error("Error converting stream data to JSON: {}", e.getMessage(), e);
        }

        return new Stream().builder()
                .type(stravaStream.getType())
                .data(dataJson) // Store the original List<Object> data
                .seriesType(stravaStream.getSeriesType())
                .originalSize(stravaStream.getOriginalSize())
                .resolution(stravaStream.getResolution())
                .activity(activity) // Assign the Activity object instead of activityId
                .build();

    }

    public String decodePolyline(String encoded) {
        StringBuilder result = new StringBuilder("");
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, value = 0;
            do {
                b = encoded.charAt(index++) - 63;
                value |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((value & 1) != 0 ? ~(value >> 1) : (value >> 1));
            lat += dlat;

            shift = 0;
            value = 0;
            do {
                b = encoded.charAt(index++) - 63;
                value |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((value & 1) != 0 ? ~(value >> 1) : (value >> 1));
            lng += dlng;

            result.append(lng / 1E5).append(",").append(lat / 1E5).append(" ");
        }

        if (result.length() > 1) {
            result.setLength(result.length() - 1); // Remove trailing comma
        }

        return result.toString();
    }

    public List<double[]> decodePolylineToCoordinates(String encoded) {
        List<double[]> coordinates = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, value = 0;
            do {
                b = encoded.charAt(index++) - 63;
                value |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((value & 1) != 0 ? ~(value >> 1) : (value >> 1));
            lat += dlat;

            shift = 0;
            value = 0;
            do {
                b = encoded.charAt(index++) - 63;
                value |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((value & 1) != 0 ? ~(value >> 1) : (value >> 1));
            lng += dlng;

            coordinates.add(new double[] { lat / 1E5, lng / 1E5 });
        }

        return coordinates;
    }

    public String scaleCoordinatesToSvg(List<double[]> coordinates, double padding, double svgSize) {
        double minLat = coordinates.stream().mapToDouble(coord -> coord[0]).min().orElse(0);
        double maxLat = coordinates.stream().mapToDouble(coord -> coord[0]).max().orElse(0);
        double minLng = coordinates.stream().mapToDouble(coord -> coord[1]).min().orElse(0);
        double maxLng = coordinates.stream().mapToDouble(coord -> coord[1]).max().orElse(0);

        double latRange = maxLat - minLat;
        double lngRange = maxLng - minLng;
        double maxRange = Math.max(latRange, lngRange);

        double xOffset = (maxRange - lngRange) / 2;
        double yOffset = (maxRange - latRange) / 2;

        StringBuilder svgPoints = new StringBuilder();
        for (double[] coord : coordinates) {
            double x = padding + ((coord[1] - minLng + xOffset) / maxRange) * (svgSize - 2 * padding);
            double y = padding + ((maxLat - coord[0] + yOffset) / maxRange) * (svgSize - 2 * padding);
            svgPoints.append(x).append(",").append(y).append(" ");
        }

        if (svgPoints.length() > 0) {
            svgPoints.setLength(svgPoints.length() - 1); // Remove trailing space
        }

        return svgPoints.toString();
    }

    public void syncActivity(Long objectId) {
        Athlete athlete = athleteRepository.findById(60270508L)
                .orElseThrow(() -> new IllegalArgumentException("Athlete not found with ID: " + 60270508L));

        buildStravaactivityFull(athlete, objectId);

        statsService.syncStatsForActivity(objectId);        

        logger.info("Activity with ID {} synchronized successfully.", objectId);
    }

}