package com.itstime.xpact.global.exception;

public class GeneralException extends RuntimeException {

    private ErrorCode errorCode;

    public GeneralException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // RuntimeException으로 전달
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public static GeneralException of(ErrorCode errorCode) {
        return new GeneralException(errorCode);
    }
}
