package org.muze.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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

    @Getter
    private String role;    //DB에 저장하지 않고, Casting 객체를 편하게 생성하기 위해 사용
}