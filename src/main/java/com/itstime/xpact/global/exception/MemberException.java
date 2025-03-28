package com.itstime.xpact.global.exception;

import lombok.Getter;

@Getter
public class MemberException extends BusinessException {

    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
