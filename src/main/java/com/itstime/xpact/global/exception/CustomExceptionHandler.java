package com.itstime.xpact.global.exception;

import com.itstime.xpact.global.response.ErrorResponse;
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

    // CustomException ( RuntimeException )
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> customException(CustomException e, HttpServletRequest request) {
        logError(e, request);
        return ErrorResponse.toResponseEntity(e.getErrorCode());
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
