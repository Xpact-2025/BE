package com.itstime.xpact.global.security.dto.request;

import com.itstime.xpact.domain.member.common.Role;
import com.itstime.xpact.domain.member.common.Type;
import com.itstime.xpact.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema
public record SignupRequestDto (
        String email,
        String password,
        String name,
        LocalDate birthDate,
        Type type,
        Role role
){
    public static Member toEntity(SignupRequestDto requestDto) {
        return Member.builder()
                .email(requestDto.email())
                .password(requestDto.password)
                .name(requestDto.name)
                .birthDate(requestDto.birthDate)
                .type(requestDto.type)
                .role(requestDto.role)
                .build();
    }
}
