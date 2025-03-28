package com.itstime.xpact.global.security.dto.response;

import com.itstime.xpact.domain.member.entity.Member;

public record SignupResponseDto (
        String email,
        String name
) {
    public static SignupResponseDto toDto(Member member) {
        return new SignupResponseDto(
                member.getEmail(),
                member.getName()
        );
    }
}
