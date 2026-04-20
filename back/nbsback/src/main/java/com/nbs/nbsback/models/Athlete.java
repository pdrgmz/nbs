package com.nbs.nbsback.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class Athlete {

    @Id
    private Long id;

    private String username;

    private String firstname;
    private String lastname;
    private String city;
    private String state;
    private String country;
    private String sex;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String profileMedium;
    private String profile;

    private String refreshToken;

    @Column(unique = true)
    private String accessToken;
}