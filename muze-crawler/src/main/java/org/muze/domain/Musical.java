package org.muze.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "musical")
@Entity
public class Musical {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "theater")
    private String theater;

    @Column(name = "poster_image")
    private String posterImage;

    @Column(name = "st_date")
    private Date stDate;

    @Column(name = "ed_date")
    private Date edDate;

    @Column(name = "view_age")
    private String viewAge;

    @Column(name = "running_time")
    private String runningTime;

    @Column(name = "main_character")
    private String mainCharacter;
}
