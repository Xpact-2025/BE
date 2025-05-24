package com.itstime.xpact.domain.experience.common;

import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.exception.ErrorCode;

public enum FormType {
    STAR_FORM, SIMPLE_FORM;

    public static void validateFormType(String formType) {
        try {
            FormType.valueOf(formType);
        } catch (Exception e) {
            throw GeneralException.of(ErrorCode.INVALID_FORMTYPE);
        }
    }
}
