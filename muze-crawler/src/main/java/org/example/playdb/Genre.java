package org.example.playdb;

import lombok.Getter;

@Getter
public enum Genre {
    LICENSE("001001"),
    ORIGINAL("001002"),
    CREATIVE("001003"),
    MUSICAL("001005");

    private final String code;

    Genre(String code) {
        this.code = code;
    }
}
