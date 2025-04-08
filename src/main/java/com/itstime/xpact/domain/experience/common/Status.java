package com.itstime.xpact.domain.experience.common;

import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;

public enum Status {
    DRAFT, SAVE;

    public static void validateStatus(String status) {
        try {
            Status.valueOf(status);
        } catch (Exception e) {
            throw CustomException.of(ErrorCode.INVALID_STATUS);
        }
    }
}
