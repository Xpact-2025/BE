package com.itstime.xpact.domain.member.dto.request;

import com.itstime.xpact.domain.member.common.SchoolStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Schema(description = "회원이 학력을 저장할 때 사용하는 DTO")
public record EducationSaveRequestDto(

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
        LocalDate endedAt
) {

        public static EducationSaveRequestDto of(String name, String major) {
                return new EducationSaveRequestDto(name, major, null, null, null);
        }

        public static EducationSaveRequestDto of(String name, String major, SchoolStatus status, LocalDate startedAt, LocalDate endedAt) {
                return new EducationSaveRequestDto(name, major, status, startedAt, endedAt);
        }
}
