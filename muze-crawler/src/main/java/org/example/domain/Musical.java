package org.example.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Musical {
    @Id
    private String id;

    @Column
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
