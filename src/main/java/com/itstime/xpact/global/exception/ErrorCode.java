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
    UNMATCHED_TOKEN(HttpStatus.UNAUTHORIZED, "TE007", "저장된 토큰과 일치하지 않습니다."),

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

    // parsing
    PARSING_ERROR(HttpStatus.BAD_REQUEST, "PE001", "파싱 중 오류가 발생하였습니다."),

    // school
    INVALID_SCHOOL_TYPE(HttpStatus.BAD_REQUEST, "SCE001", "올바른 타입을 입력해주세요."),

    // server
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SE001", "Internal Server Error"),

    // openai
    OPENAI_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OPENAI001", "OpenAI Error"),
    FAILED_OPENAI_PARSING(HttpStatus.BAD_REQUEST, "OPENAI002", "OpenAI의 응답을 파싱할 수 없습니다."),
    FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "OPENAI003", "업로드할 파일이 존재하지 않습니다."),
    UNMATCHED_OPENAI_FORMAT(HttpStatus.BAD_REQUEST, "OPENAI004", "OpenAI 응답이 형식에 맞지 않습니다."),

    // crawling
    CRAWLING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CRAWL001", "Crawling Error"),

    // education
    EDUCATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "EE001", "저장된 학력 사항이 없습니다."),
    INSUFFICIENT_SCHOOL_INFO(HttpStatus.BAD_REQUEST, "EE002", "학력 정보가 부족합니다."),

    // recruit
    RECRUIT_NOT_FOUND(HttpStatus.BAD_REQUEST, "RE001", "해당 직무가 존재하지 않습니다."),
    EMPTY_DESIRED_RECRUIT(HttpStatus.BAD_REQUEST, "RE002", "희망 직무가 선택되지 않았습니다."),

    // detail recruit
    DETAIL_RECRUIT_NOT_FOUND(HttpStatus.BAD_REQUEST, "DR001", "해당 상세직무가 존재하지 않습니다."),

    // keyword
    KEYWORD_EXCEEDED(HttpStatus.BAD_REQUEST, "EXP009", "키워드는 5개를 넘길 수 없습니다."),
    KEYWORD_TOO_LONG(HttpStatus.BAD_REQUEST, "EXP010", "키워드는 10글자를 넘길 수 없습니다."),

    // ratio
    NO_EXPERIENCE(HttpStatus.BAD_REQUEST, "RAT001", "분석할 경험이 없습니다."),

    // coreSkill
    NOT_FOUND_CORESKILLS(HttpStatus.BAD_REQUEST, "CSE001", "해당 핵심 역량을 불러오는 데에 실패했습니다."),

    // skill map
    UNLOADED_SKILL_MAP(HttpStatus.BAD_REQUEST, "SME001", "핵심 스킬맵 로드에 실패하였습니다."),
    EXPERIENCES_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "SME002", "입력된 경험이 충분하지 않습니다."),
    EMPTY_STRENGTH(HttpStatus.NOT_FOUND, "SME003", "핵심역량 중 강점 부분이 저장되지 않았습니다."),
    EMPTY_WEAKNESS(HttpStatus.NOT_FOUND, "SME004", "핵심역량 중 약점 부분이 저장되지 않았습니다."),
    FEEDBACK_SAVE_ERROR(HttpStatus.BAD_REQUEST, "SME005", "핵심역량의 강점 및 약점을 저장하는 데에 오류가 발생하였습니다."),
    SKILLMAP_NOT_FOUND(HttpStatus.NOT_FOUND, "SME006", "저장된 핵심스킬맵이 존재하지 않습니다."),

    // Async
    FAILED_GET_RESULT(HttpStatus.BAD_REQUEST, "AE001", "요청에 대한 응답을 얻지 못했습니다."),
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "AE002", "요청 시간이 초과되었습니다."),
    REJECTED_QUEUE(HttpStatus.TOO_MANY_REQUESTS, "AE003", "서버가 현재 요청을 처리할 수 없습니다. 잠시 뒤에 다시 시도해주세요."),

    // json
    FAILED_DESERIALIZE(HttpStatus.BAD_REQUEST, "JE001", "역직렬화에 실패하였습니다."),
    FAILED_SERIALIZE(HttpStatus.BAD_REQUEST, "JE002", "직렬화에 실패하였습니다."),

    // trie
    NO_SEARCH_RESULT(HttpStatus.NOT_FOUND, "TRE001", "검색 결과 조회된 데이터가 없습니다."),

    // weakness
    WEAKNESS_NOT_FOUND(HttpStatus.NOT_FOUND, "WE001", "해당 약점에 대한 DB 조회가 불가능합니다."),
    NEED_ANALYSIS(HttpStatus.BAD_REQUEST, "WE002", "경험에 대한 분석을 우선으로 진행해주세요."),
    NEED_THREE_WEAKNESS(HttpStatus.BAD_REQUEST, "WE003", "약점에 대한 DB가 불충분합니다. 핵심스킬맵의 분석을 다시 진행해주세요."),

    // file
    NO_SUCH_FILE(HttpStatus.NOT_FOUND, "F001", "해당 파일은 존재하지 않습니다."),

    // etc
    INVALID_DATE(HttpStatus.BAD_REQUEST, "ETC001", "잘못된 날짜입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
