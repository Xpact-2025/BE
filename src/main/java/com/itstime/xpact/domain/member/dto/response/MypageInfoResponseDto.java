package com.itstime.xpact.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "회원 프로필 조회 정보")
@Builder
public record MypageInfoResponseDto (
        @Schema(description = "회원 이름",
                example = "홍길동")
        String name,

        @Schema(description = "사진 이미지 URL")
        String imgurl,

        @Schema(description = "회원 나이")
        int age,

        @Schema(description = "학위 구분",
        example = "대학교")
        String educationDegree,

        @Schema(description = "회원 최종 학력 한 줄",
                example = "잇타대학교 잇타학과 재학")
        String educationName,

        @Schema(description = "재학 / 휴학 ")
        String schoolState,

        @Schema(description = "희망 직무",
                example = "서비스 기획자")
        String desiredDetailRecruit
) {
}
