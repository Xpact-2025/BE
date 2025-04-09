package com.itstime.xpact.domain.experience.common;

import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;

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
            throw CustomException.of(ErrorCode.INVALID_EXPERIENCE_TYPE);
        }
    }
}
