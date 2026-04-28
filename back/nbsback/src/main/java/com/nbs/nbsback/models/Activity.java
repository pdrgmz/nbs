package com.nbs.nbsback.models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "activity", indexes = {
    @Index(name = "idx_athlete_id", columnList = "athleteId"),
    @Index(name = "idx_start_date", columnList = "startDate"),
    @Index(name = "idx_start_date_local", columnList = "startDateLocal"),
    @Index(name = "idx_type", columnList = "type")
})
public class Activity {

    @Id
    private Long id;

    private Long athleteId;

    private String name;
    private double distance;
    private Integer movingTime;
    private Integer elapsedTime;
    private double totalElevationGain;
    private String type;
    private String sportType;
    private String deviceName;
    private LocalDateTime startDate;
    private LocalDateTime startDateLocal;
    private String timezone;
    private Double utcOffset;
    private List<Double> startLatlng;
    private List<Double> endLatlng;
    private double averageSpeed;
    private double maxSpeed;
    private double averageCadence;
    private boolean hasHeartrate;
    private double averageHeartrate;
    private Double maxHeartrate;
    private double elevHigh;
    private double elevLow;

    //Map fields
    @Column(columnDefinition = "TEXT")
    private String polyline;

    @Column(columnDefinition = "TEXT")
    private String summaryPolyline;

    @Column(columnDefinition = "TEXT")
    private String polylinePoints;

    @Column(columnDefinition = "TEXT")
    private String summaryPolylinePoints;

}
