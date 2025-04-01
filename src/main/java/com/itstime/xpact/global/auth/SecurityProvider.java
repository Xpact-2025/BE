package com.itstime.xpact.global.auth;

import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityProvider {

    public Long getCurrentMemberId() {
        MemberAuthentication authentication = (MemberAuthentication) SecurityContextHolder.getContext().getAuthentication();

        try {
            return authentication.getMember().getId();
        } catch (Exception e) {
            throw CustomException.of(ErrorCode.MEMBER_NOT_EXISTS);
        }
    }
}
