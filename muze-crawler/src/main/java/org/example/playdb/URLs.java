package org.example.playdb;

public final class URLs {
    // 뮤지컬 목록 페이지
    public static final String MUSICAL_URL = "https://www.playdb.co.kr/playdb/playdblist.asp";
    public static final String PLAY_TYPE = "sPlayType=";
    public static final String SUB_CATEGORY = "sReqSubCategory=";
    public static final String PAGE = "Page=";


    // 뮤지컬 상세 페이지
    public static final String MUSICAL_DETAIL_URL = "https://www.playdb.co.kr/playdb/playdbDetail.asp";
    public static final String PLAY_NO = "sReqPlayno=";

    public static String getMusicalUrl(LookupType lookupType, Genre genre) {
        return MUSICAL_URL + "?" + PLAY_TYPE + lookupType.getCode() + "&" + SUB_CATEGORY + genre.getCode();
    }

    public static String getMusicalDetailUrl(String musicalId) {
        return MUSICAL_DETAIL_URL + "?" + PLAY_NO + musicalId;
    }
}
