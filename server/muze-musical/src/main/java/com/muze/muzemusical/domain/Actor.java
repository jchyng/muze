package com.muze.muzemusical.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Actor {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column
    private String profileImage;
}

