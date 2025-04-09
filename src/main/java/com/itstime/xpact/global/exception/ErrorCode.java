package com.itstime.xpact.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    TEST(HttpStatus.INTERNAL_SERVER_ERROR, "500 에러", "Test Error"),

    // experience
    EXPERIENCE_NOT_EXISTS(HttpStatus.BAD_REQUEST, "EXP001", "해당 경험이 존재하지 않습니다."),
    INVALID_FORMTYPE(HttpStatus.BAD_REQUEST, "EXP002", "잘못된 FormType입니다."),
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "EXP003", "잘못된 Status입니다."),
    STATUS_NOT_CONSISTENCY(HttpStatus.BAD_REQUEST, "EXP004", "Status Not Consistency"),
    NOT_YOUR_EXPERIENCE(HttpStatus.BAD_REQUEST, "EXP005", "본인의 Experience가 아닙니다."),
    INVALID_ORDER(HttpStatus.BAD_REQUEST, "EXP006", "잘못된 order입니다."),
    INVALID_EXPERIENCE_TYPE(HttpStatus.BAD_REQUEST, "EXP007", "잘못된 ExperienceType입니다."),
    INVALID_SAVE(HttpStatus.BAD_REQUEST, "EXP008", "저장된 경험은 임시저장될 수 없습니다."),

    // token
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TE001", "토큰이 존재하지 않습니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "TE002", "토큰의 서명이 유효하지 않습니다."),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "TE003", "유효하지 않은 토큰입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "TE004", "토큰의 기한이 만료되었습니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "TE005", "지원하지 않는 토큰 형식입니다."),
    FAILED_JWT_INFO(HttpStatus.UNAUTHORIZED, "TE006", "토큰으로부터 회원의 정보를 얻을 수 없습니다."),

    // login
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "권한이 존재하지 않습니다"),
    UNMATCHED_PASSWORD(HttpStatus.UNAUTHORIZED, "PWD001", "비밀번호가 일치하지 않습니다."),
    EMPTY_COOKIE(HttpStatus.UNAUTHORIZED, "CE001", "쿠키가 존재하지 않습니다."),
    PLATFORM_NOT_FOUND(HttpStatus.BAD_REQUEST, "LE001", "해당 플랫폼이 존재하지 않습니다."),
    ACCESS_TOKEN_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "LE002", "서버로부터 토큰을 받지 못하였습니다."),
    FAILED_MEMBER_INFO(HttpStatus.BAD_REQUEST, "LE003", "서버로부터 프로필을 조회하지 못하였습니다."),

    // category
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "CAT001", "Invalid Category"),

    // member
    MEMBER_NOT_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER001", "Member Not Exists"),
    MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER002", "이미 존재하는 회원입니다."),

    // server
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SE001", "Internal Server Error"),

    // openai
    OPENAI_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OPENAI001", "OpenAI Error"),

    // crawling
    CRAWLING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CRAWL001", "Crawling Error"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
