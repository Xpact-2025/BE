package com.itstime.xpact.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    TEST(HttpStatus.INTERNAL_SERVER_ERROR, "500 에러", "Test Error"),

    EXPERIENCE_NOT_EXISTS(HttpStatus.NO_CONTENT, "EXP001", "Experience Not Exists"),
    INVALID_FORMTYPE(HttpStatus.BAD_REQUEST, "EXP002", "Invalid FormType"),
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "EXP003", "Invalid Status"),


    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
