package org.muze.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

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
