package com.itstime.xpact.domain.member.dto.response;

import com.itstime.xpact.domain.member.common.SchoolStatus;
import com.itstime.xpact.domain.member.entity.Education;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "회원 학력 저장에 사용되는 DTO")
public record EducationSaveResponseDto(
        @Schema(description = "학교 이름",
                example = "잇타대학교")
        String name,

        @Schema(description = "학과 이름",
                example = "잇타학과")
        String major,

        @Schema(description = "현재 상태",
                example = "CURRENT",
                allowableValues = {"CURRENT", "GRADUATION", "SUSPENDED"})
        SchoolStatus schoolStatus,

        @Schema(description = "입학 날짜",
                example = "2025-03-02")
        LocalDate startedAt,

        @Schema(description = "졸업 날짜(Null 허용)",
                example = "2025-03-02")
        LocalDate endedAt,

        @Schema(description = "최종학력 한 줄 ( 마이페이지에 최종 반영 내용 )",
        example = "잇타대학교 잇타학과 재학")
        String educationName
) {
}
