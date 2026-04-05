package com.nbs.nbsback.stravamodels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StravaStream {

    private String type;

    private List<Object> data;

    private String seriesType;
    private int originalSize;
    private String resolution;

}