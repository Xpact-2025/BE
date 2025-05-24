package com.itstime.xpact.global.auth;

import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityProvider {

    private final MemberRepository memberRepository;

    // 인증된 MemberId 반환
    public Long getCurrentMemberId() {
        return getMemberFromSecurityContext().getId();
    }

    // 현재 인증된 Member를 반환
    public Member getCurrentMember() {
        return memberRepository.findById(getMemberFromSecurityContext().getId()).orElseThrow(() ->
                GeneralException.of(ErrorCode.MEMBER_NOT_EXISTS));
    }

    private Member getMemberFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof MemberAuthentication memberAuth)) {
            throw GeneralException.of(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        Member member = memberAuth.getMember();
        if (member == null) {
            throw GeneralException.of(ErrorCode.MEMBER_NOT_EXISTS);
        }
        return member;
    }
}
