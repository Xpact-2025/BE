package com.itstime.xpact.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 학력 저장에 사용되는 DTO")
public record SchoolSearchResponseDto(
        @Schema(description = "학교 이름",
                example = "잇타대학교")
        String name,

        @Schema(description = "학과 이름",
                example = "잇타학과")
        String major
) {
}
