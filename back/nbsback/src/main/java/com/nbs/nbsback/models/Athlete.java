package com.nbs.nbsback.models;

import java.time.LocalDateTime;

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
@Table(name = "athlete", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_created_at", columnList = "createdAt"),
    @Index(name = "idx_updated_at", columnList = "updatedAt")
})
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