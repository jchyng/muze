package org.muze.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.Objects;

@Getter
@Builder
@AllArgsConstructor
public class Musical {

    private String id;
    private String title;
    private String theater;
    private String posterImage;
    private Date stDate;
    private Date edDate;
    private String viewAge;
    private String runningTime;
    private String mainCharacter;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Musical musical = (Musical) o;
        return Objects.equals(id, musical.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
