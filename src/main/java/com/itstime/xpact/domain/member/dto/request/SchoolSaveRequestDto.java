package com.itstime.xpact.domain.member.dto.request;

import com.itstime.xpact.domain.member.common.SchoolStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원이 학력을 저장할 때 사용하는 DTO")
public record SchoolSaveRequestDto(

        @Schema(description = "학교 이름",
        example = "잇타대학교")
        String name,

        @Schema(description = "학과 이름",
        example = "잇타학과")
        String major,

        @Schema(description = "현재 상태",
        example = "재학 중")
        SchoolStatus schoolStatus
) {
}
