package com.itstime.xpact.domain.member.dto.response;

import com.itstime.xpact.domain.member.common.Degree;
import com.itstime.xpact.domain.member.common.SchoolStatus;
import com.itstime.xpact.domain.member.entity.Education;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "회원 학력 저장에 사용되는 DTO")
public record EducationSaveResponseDto(

        @Schema(description = "학위 구분",
                example = "UNIV",
                allowableValues = {"UNIV", "HIGH", "GRADUATE", "MASTER", "DOCTOR"})
        Degree degree,

        @Schema(description = "학교 이름",
                example = "잇타대학교")
        String name,

        @Schema(description = "학과 이름",
                example = "잇타학과")
        String major,

        @Schema(description = "현재 상태",
                example = "CURRENT",
                allowableValues = {"CURRENT", "GRADUATION", "SUSPENDED", "EXPECTED_GRADUATION", "COMPLETE", "WITHDRAWN"})
        SchoolStatus schoolStatus,

        @Schema(description = "최종학력 한 줄 ( 마이페이지에 최종 반영 내용 )",
        example = "잇타대학교 잇타학과 재학")
        String educationName
) {

        public static EducationSaveResponseDto toDto(Education education) {
                return EducationSaveResponseDto.builder()
                        .degree(education.getDegree())
                        .name(education.getSchoolName())
                        .major(education.getMajor())
                        .schoolStatus(education.getSchoolStatus())
                        .educationName(education.getEducationName())
                        .build();
        }
}
