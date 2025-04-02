package com.itstime.xpact.domain.experience.dto;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Schema(description = "경험 생성 요청 DTO")
public class ExperienceCreateRequestDto {

    // common (공통 부분)
    @Schema(description = "경험 저장 방식 지정 (임시저장 or 저장)", example = "SAVE", allowableValues = {"SAVE", "DRAFT"})
    private Status status;

    @Schema(description = "경험 유형 지정",
            example = "INTERN",
            allowableValues = {"INTERN", "EXTERNAL_ACTIVITIES", "CONTEST",
                    "PROJECT", "CERTIFICATES", "ACADEMIC_CLUB", "EDUCATION",
                    "PRIZE", "VOLUNTEER_WORK", "STUDY_ABROAD", "ETC"})
    private ExperienceType experienceType;

    @Schema(description = "경험 양식 지정 (Star양식 or 간결 양식)", example = "STAR_FORM")
    private FormType formType;

    @Schema(description = "경험 제목", example = "제목을 입력하세요")
    private String title;

    @Schema(description = "시작 일시", example = "2025-03-27")
    private LocalDate startDate;

    @Schema(description = "종료 일시", example = "2025-03-27")
    private LocalDate endDate;

    @Schema(description = "키워드", example = "키워드를 입력하세요")
    private String keyword;

    // STAR_FORM
    @Schema(description = "상황", example = "상황을 입력하세요")
    private String situation;

    @Schema(description = "문제", example = "문제를 입력하세요")
    private String task;

    @Schema(description = "해결방법", example = "해결방법을 입력하세요")
    private String action;

    @Schema(description = "결과", example = "결과를 입력하세요")
    private String result;

    // SIMPLE_FORM
    @Schema(description = "역할", example = "역할을 입력하세요")
    private String role;

    @Schema(description = "주요성과", example = "주요성과를 입력하세요")
    private String perform;
}
