package com.itstime.xpact.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "회원 프로필 설정 DTO")
public record MemberInfoResponseDto (
        @Schema(description = "회원 이름",
        example = "홍길동")
        String name,

        @Schema(description = "사진 이미지 URL")
        String imgurl,

        @Schema(description = "회원 나이")
        int age,

        @Schema(description = "회원 최종 학력 한 줄",
                example = "잇타대학교 잇타학과 재학")
        String education,

        @Schema(description = "희망 직무",
        example = "서비스 기획자")
        String desiredDetailRecruit,

        @Schema(description = "입학 날짜",
                example = "2025-03-02")
        LocalDate startedAt,

        @Schema(description = "졸업 날짜",
                example = "2025-03-02")
        LocalDate endedAt
) {
}
