package com.nbs.nbsback.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long activityId;
    private String type;
}