package com.nbs.nbsback.stravamodels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StravaAthlete {

    private Long id;
    private String username;
    private Integer resourceState;
    private String firstname;
    private String lastname;
    private String bio;
    private String city;
    private String state;
    private String country;
    private String sex;
    private boolean premium;
    private boolean summit;
    private String createdAt;
    private String updatedAt;
    private Integer badgeTypeId;
    private double weight;
    private String profileMedium;
    private String profile;
    private Boolean friend;
    private Boolean follower;
    private boolean blocked;
    private boolean canFollow;
    private Integer followerCount;
    private Integer friendCount;
    private Integer mutualFriendCount;
    private Integer athleteType;
    private String datePreference;
    private String measurementPreference;
    private List<StravaClub> clubs;
    private Integer postableClubsCount;
    private Object ftp;
    private List<Object> bikes;
    private List<Object> shoes;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StravaClub {
        private Long id;
        private Integer resourceState;
        private String name;
        private String profileMedium;
        private String profile;
        private String coverPhoto;
        private String coverPhotoSmall;
        private List<String> activityTypes;
        private String activityTypesIcon;
        private List<String> dimensions;
        private String sportType;
        private String localizedSportType;
        private String city;
        private String state;
        private String country;
        private boolean isPrivate;
        private Integer memberCount;
        private boolean featured;
        private boolean verified;
        private String url;
        private String membership;
        private boolean admin;
        private boolean owner;
    }
}