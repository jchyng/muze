package com.muze.muzemusical.domain;

import jakarta.persistence.*;

@Entity
public class Casting {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private Actor actorId;

    @Column
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private Musical musicalId;
}
