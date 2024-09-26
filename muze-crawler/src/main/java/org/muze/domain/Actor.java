package org.muze.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Actor {

    @Id
    private String id;

    @Column
    private String name;

    @Column
    private String profileImage;

    @Column
    private String role;


}