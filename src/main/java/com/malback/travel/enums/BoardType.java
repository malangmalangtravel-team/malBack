package com.malback.travel.enums;

public enum BoardType {
    //GENERAL("일반 게시판"),
    INFO("정보 게시판"),
    FREE("자유 게시판"),
    YOUTUBE("여행 유튜브"),
    USED_TRADE("중고거래"),
    MATE("동행");

    private final String description;

    BoardType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
