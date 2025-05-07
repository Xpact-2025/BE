package com.itstime.xpact.domain.member.common;

public enum SchoolStatus {
    CURRENT("재학"),
    GRADUATION("졸업"),
    SUSPENDED("휴학");

     private final String displayName;

    SchoolStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
