package com.malback.user.enums;

public enum Role {
    USER("일반 유저"),
    ADMIN("관리자");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
