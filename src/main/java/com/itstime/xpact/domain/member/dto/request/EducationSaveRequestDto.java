package com.itstime.xpact.domain.member.dto.request;

import com.itstime.xpact.domain.member.common.Degree;
import com.itstime.xpact.domain.member.common.SchoolStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원이 학력을 저장할 때 사용하는 DTO")
public record EducationSaveRequestDto(

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
        SchoolStatus schoolStatus
) {
        public static EducationSaveRequestDto of(Degree degree, String name, String major, SchoolStatus schoolStatus) {
                return new EducationSaveRequestDto(degree, name, major, schoolStatus);
        }
}
