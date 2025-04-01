package com.itstime.xpact.global.response;

import com.itstime.xpact.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private HttpStatus httpStatus;
    private String code;
    private String message;

    public ErrorResponse(ErrorCode errorCode) {

        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    // ResponseEntity를 이용한 반환
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .httpStatus(errorCode.getHttpStatus())
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
                );
    }
}
