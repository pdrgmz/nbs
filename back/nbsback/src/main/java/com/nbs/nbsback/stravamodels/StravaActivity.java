package com.nbs.nbsback.stravamodels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StravaActivity {

    private int resourceState;
    private Athlete athlete;
    private String name;
    private double distance;
    private int movingTime;
    private int elapsedTime;
    private double totalElevationGain;
    private String type;
    private String sportType;
    private String workoutType;
    private String deviceName;
    private long id;
    private String startDate;
    private String startDateLocal;
    private String timezone;
    private double utcOffset;
    private String locationCity;
    private String locationState;
    private String locationCountry;
    private int achievementCount;
    private int kudosCount;
    private int commentCount;
    private int athleteCount;
    private int photoCount;
    private Map map;
    private boolean trainer;
    private boolean commute;
    private boolean manual;
    private boolean privateActivity;
    private String visibility;
    private boolean flagged;
    private String gearId;
    private List<Double> startLatlng;
    private List<Double> endLatlng;
    private double averageSpeed;
    private double maxSpeed;
    private double averageCadence;
    private boolean hasHeartrate;
    private double averageHeartrate;
    private double maxHeartrate;
    private boolean heartrateOptOut;
    private boolean displayHideHeartrateOption;
    private double elevHigh;
    private double elevLow;
    private long uploadId;
    private String uploadIdStr;
    private String externalId;
    private boolean fromAcceptedTag;
    private int prCount;
    private int totalPhotoCount;
    private boolean hasKudoed;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Athlete {
        private long id;
        private int resourceState;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Map {
        private String id;
        private String summaryPolyline;
        private int resourceState;
    }
}