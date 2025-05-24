package com.itstime.xpact.global.exception;

import com.itstime.xpact.global.response.ErrorResponse;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Hidden
@RestControllerAdvice
public class CustomExceptionHandler {

    // GeneralException ( RuntimeException )
    @ExceptionHandler(GeneralException.class)
    protected ResponseEntity<ErrorResponse> generalException(GeneralException e, HttpServletRequest request) {
        logError(e, request);
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    // CustomException - 상태코드는 200이지만, ErrorCode에 대한 예외를 처리
    protected ResponseEntity<RestResponse<Void>> customException(CustomException e, HttpServletRequest request) {
        logError(e, request);
        return ResponseEntity.ok(
                RestResponse.onFailure(e.getErrorCode())
        );
    }

    // Header missing Exception
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ErrorResponse> missingRequestHeaderException(MissingRequestHeaderException e, HttpServletRequest request) {
        logError(e, request);
        return ErrorResponse.toResponseEntity(ErrorCode.TOKEN_NOT_FOUND);
    }

    // 기타 예외들( NullPointerException, IllegalArgumentException 등 )
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        logError(e, request);
        return ErrorResponse.toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
    }


    // console log 출력
    private void logError(Exception e, HttpServletRequest request) {
        log.error("Request URI : [{}] {}", request.getMethod(), request.getRequestURI());
        log.error("Exception : ", e);
    }
}
