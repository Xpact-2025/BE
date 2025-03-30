package com.itstime.xpact.global.response;

import com.itstime.xpact.global.exception.ErrorCode;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResponse {

    private HttpStatus httpStatus;
    private String code;
    private String message;

    public ErrorResponse(ErrorCode errorCode) {

        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public void addMessage(String message) {
        this.message = this.message + ":" + message;
    }
}
