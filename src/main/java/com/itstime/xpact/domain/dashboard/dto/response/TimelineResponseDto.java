package com.itstime.xpact.domain.dashboard.dto.response;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "경험에 대한 타임라인 조회 응답 DTO")
public record TimelineResponseDto (
        @Schema(description = "경험 시작 날짜",
        example = "2025-03-20")
        LocalDate startDate,
        @Schema(description = "경험 종료 날짜",
        example = "2025-07-12")
        LocalDate endDate,
        @Schema(description = "경험 제목",
        example = "IT 연합 동아리 ITTA")
        String title,
        @Schema(description = "경험 유형",
                example = "EXTERNAL_ACTIVITIES",
                allowableValues = {"INTERN", "EXTERNAL_ACTIVITIES", "CONTEST",
                        "PROJECT", "CERTIFICATES", "ACADEMIC_CLUB", "EDUCATION",
                        "PRIZE", "VOLUNTEER_WORK", "STUDY_ABROAD", "ETC"})
        ExperienceType experienceType
        ) {
}
