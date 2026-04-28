package com.nbs.nbsback.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@IdClass(StreamId.class)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stream", indexes = {
    @Index(name = "idx_series_type", columnList = "seriesType"),
    @Index(name = "idx_resolution", columnList = "resolution")
})
public class Stream {

    @Id
    private String type;

    @Id
    private Long activityId;

    @Column(columnDefinition = "TEXT")  
    private String data;

    private String seriesType;
    private int originalSize;
    private String resolution;
}