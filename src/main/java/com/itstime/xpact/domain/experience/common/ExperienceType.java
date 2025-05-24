package com.itstime.xpact.domain.experience.common;

import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.exception.ErrorCode;

import java.util.Set;

public enum ExperienceType {
    INTERN,
    EXTERNAL_ACTIVITIES,
    CONTEST,
    PROJECT,
    CERTIFICATES,
    ACADEMIC_CLUB,
    EDUCATION,
    PRIZE,
    VOLUNTEER_WORK,
    STUDY_ABROAD,
    ETC,;

    public static void validateExperienceType(String experienceType) {
        try {
            ExperienceType.valueOf(experienceType);
        } catch (Exception e) {
            throw GeneralException.of(ErrorCode.INVALID_EXPERIENCE_TYPE);
        }
    }

    public static final Set<ExperienceType> IS_QUALIFICATION = Set.of(CERTIFICATES, PRIZE);
    public static final Set<ExperienceType> NEED_WARRNT = Set.of(INTERN, EXTERNAL_ACTIVITIES, EDUCATION);
    public static final Set<ExperienceType> NOT_NEED_FILE = Set.of(VOLUNTEER_WORK, STUDY_ABROAD, ETC);
}
