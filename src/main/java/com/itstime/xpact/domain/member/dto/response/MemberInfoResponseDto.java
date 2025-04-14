package com.itstime.xpact.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

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

        @Schema(description = "회원 학력 이름",
        example = "잇타대학교 잇타학과 재학")
        String education,

        @Schema(description = "희망 직무",
        example = "회계사")
        String recruit
) {
}
