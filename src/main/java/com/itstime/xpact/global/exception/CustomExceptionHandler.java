package com.itstime.xpact.global.exception;

import com.itstime.xpact.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    // CustomException ( RuntimeException )
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> customException(CustomException e, HttpServletRequest request) {
        return buildErrorResponse(e.getErrorCode(), request);
    }

    // Header missing Exception
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ErrorResponse> missingRequestHeaderException(MissingRequestHeaderException e, HttpServletRequest request) {

        return buildErrorResponse(ErrorCode.TOKEN_NOT_FOUND, request);
    }

    // 기타 예외들( NullPointerException, IllegalArgumentException 등 )
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Unhandled Exception : ", e);
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, request);
    }


    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode, HttpServletRequest request) {

        log.error("Request URI : [{}] {}", request.getMethod(), request.getRequestURI());
        log.error("Error message : {}", errorCode.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }
}
