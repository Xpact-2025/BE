package com.itstime.xpact.domain.experience.common;

import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.exception.ErrorCode;

public enum Status {
    DRAFT, SAVE;

    public static void validateStatus(String status) {
        try {
            Status.valueOf(status);
        } catch (Exception e) {
            throw GeneralException.of(ErrorCode.INVALID_STATUS);
        }
    }
}
