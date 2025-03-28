package com.itstime.xpact.global.security.dto.request;

import com.itstime.xpact.domain.member.entity.Member;

import java.time.LocalDate;

public record SignupRequestDto (
        String email,
        String password,
        String name,
        LocalDate birthDate
){
    public static Member toEntity(SignupRequestDto requestDto) {
        return Member.builder()
                .email(requestDto.email())
                .password(requestDto.password)
                .name(requestDto.name)
                .birthDate(requestDto.birthDate)
                .build();
    }
}
