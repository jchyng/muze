package com.muze.muzemusical.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class Musical {
    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column
    private String theater;

    @Column
    private String posterImage;

    @Column
    private Date stDate;

    @Column
    private Date edDate;

    @Column
    private String viewAge;

    @Column
    private String runningTime;

    @Column
    private String mainCharacter;
}
