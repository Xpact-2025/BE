package com.itstime.xpact.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    TEST(HttpStatus.INTERNAL_SERVER_ERROR, "500 에러", "Test Error"),

    // token
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "TE001", "토큰의 서명이 유효하지 않습니다."),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "TE002", "유효하지 않은 토큰입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "TE003", "토큰의 기한이 만료되었습니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "TE004", "지원하지 않는 토큰 형식입니다."),
    EMPTY_JWT(HttpStatus.BAD_REQUEST, "TE005", "토큰이 존재하지 않습니다.");
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
