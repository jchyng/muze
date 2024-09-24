package org.example.playdb;

import lombok.Getter;

/** PlayDB 크롤링 시 조회 대상의 유형
 * 모든 뮤지컬과 최근 업데이트된 뮤지컬을 구분하기 위한 enum
 * */
@Getter
public enum LookupType {
    ALL("1"), NEW("3");

    private final String code;

    LookupType(String code) {
        this.code = code;
    }
}
