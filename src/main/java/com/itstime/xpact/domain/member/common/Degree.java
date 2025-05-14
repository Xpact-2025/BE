package com.itstime.xpact.domain.member.common;

public enum Degree {
    HIGH("고등학교"),
    UNIV("대학교"),
    GRADUATE("대학원"),
    MASTER("대학원(석사)"),
    DOCTOR("대학원(박사)"),
    ;

    private final String displayName;

    Degree(final String displayName) { this.displayName = displayName; }

    public String getDisplayName() { return this.displayName; }
}
