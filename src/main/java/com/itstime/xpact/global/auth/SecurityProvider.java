package com.itstime.xpact.global.auth;

import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityProvider {

    public Long getCurrentMemberId() {
        return getCurrentMember().getId();
    }

    // 현재 인증된 Member를 반환
    public Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof MemberAuthentication memberAuth)) {
            throw CustomException.of(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        Member member = memberAuth.getMember();
        if (member == null) {
            throw CustomException.of(ErrorCode.MEMBER_NOT_EXISTS);
        }
        return member;
    }
}
