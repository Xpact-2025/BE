package com.itstime.xpact.global.exception;

public class CustomException extends RuntimeException {

    private ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public static CustomException of(ErrorCode errorCode) {
        return new CustomException(errorCode);
    }
}
