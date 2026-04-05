package com.nbs.nbsback.stravamodels;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nbs.nbsback.stravamodels.StravaActivityDetail.SegmentEffort.Activity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StravaActivityDetail {

    private int resourceState;
    private Athlete athlete;
    private String name;
    private double distance;
    private int movingTime;
    private int elapsedTime;
    private double totalElevationGain;
    private String type;
    private String sportType;
    private String deviceName;
    private long id;
    private LocalDateTime startDate;
    private LocalDateTime startDateLocal;
    private String timezone;
    private Double utcOffset;
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
    private boolean isPrivate;
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
    private String description;
    private double calories;
    private Object perceivedExertion;
    private boolean preferPerceivedExertion;
    private List<SegmentEffort> segmentEfforts;
    private List<SplitMetric> splitsMetric;
    private List<SplitStandard> splitsStandard;
    private List<Lap> laps;
    private List<BestEffort> bestEfforts;

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
        private String polyline;
        private int resourceState;
        private String summaryPolyline;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SegmentEffort {
        private long id;
        private int resourceState;
        private String name;
        private Activity activity;
        private Athlete athlete;
        private int elapsedTime;
        private int movingTime;
        private LocalDateTime startDate;
        private LocalDateTime startDateLocal;
        private double distance;
        private int startIndex;
        private int endIndex;
        private double averageCadence;
        private boolean deviceWatts;
        private double averageHeartrate;
        private double maxHeartrate;
        private Segment segment;
        private Integer prRank;
        private List<Achievement> achievements;
        private String visibility;
        private boolean hidden;

        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Activity {
            private long id;
            private String visibility;
            private int resourceState;
        }

        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Segment {
            private long id;
            private int resourceState;
            private String name;
            private String activityType;
            private double distance;
            private double averageGrade;
            private double maximumGrade;
            private double elevationHigh;
            private double elevationLow;
            private List<Double> startLatlng;
            private List<Double> endLatlng;
            private Object elevationProfile;
            private Object elevationProfiles;
            private int climbCategory;
            private String city;
            private String state;
            private String country;
            private boolean isPrivate;
            private boolean hazardous;
            private boolean starred;
        }

        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Achievement {
            private int typeId;
            private String type;
            private int rank;
        }
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SplitMetric {
        private double distance;
        private int elapsedTime;
        private double elevationDifference;
        private int movingTime;
        private int split;
        private double averageSpeed;
        private double averageGradeAdjustedSpeed;
        private double averageHeartrate;
        private int paceZone;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SplitStandard {
        private double distance;
        private int elapsedTime;
        private double elevationDifference;
        private int movingTime;
        private int split;
        private double averageSpeed;
        private double averageGradeAdjustedSpeed;
        private double averageHeartrate;
        private int paceZone;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Lap {
        private long id;
        private int resourceState;
        private String name;
        private Activity activity;
        private Athlete athlete;
        private int elapsedTime;
        private int movingTime;
        private LocalDateTime startDate;
        private LocalDateTime startDateLocal;
        private double distance;
        private double averageSpeed;
        private double maxSpeed;
        private int lapIndex;
        private int split;
        private int startIndex;
        private int endIndex;
        private double totalElevationGain;
        private double averageCadence;
        private boolean deviceWatts;
        private double averageHeartrate;
        private double maxHeartrate;
        private int paceZone;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BestEffort {
        private long id;
        private int resourceState;
        private String name;
        private Activity activity;
        private Athlete athlete;
        private int elapsedTime;
        private int movingTime;
        private LocalDateTime startDate;
        private LocalDateTime startDateLocal;
        private double distance;
        private Integer prRank;
        private List<SegmentEffort.Achievement> achievements;
        private int startIndex;
        private int endIndex;
    }
}