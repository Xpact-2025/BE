package com.itstime.xpact.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원이 학력을 저장할 때 사용하는 DTO")
public record SchoolInfoRequestDto(

        @Schema(description = "학교 이름",
        example = "잇타대학교")
        String name,

        @Schema(description = "학과 이름",
        example = "잇타학과")
        String major
) {
}
