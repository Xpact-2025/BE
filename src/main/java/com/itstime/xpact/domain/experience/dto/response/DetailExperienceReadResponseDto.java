package com.itstime.xpact.domain.experience.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.entity.*;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.itstime.xpact.domain.experience.common.ExperienceType.IS_QUALIFICATION;
import static com.itstime.xpact.domain.experience.common.FormType.SIMPLE_FORM;
import static com.itstime.xpact.domain.experience.common.FormType.STAR_FORM;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailExperienceReadResponseDto {

    // 공통 부분
    private Long id;
    private Status status;
    private FormType formType;
    private ExperienceType experienceType;

    private Boolean isEnded;
    private LocalDate startDate;
    private LocalDate endDate;
    private String title;

    // STAR 양식 부분
    private String situation;
    private String task;
    private String action;
    private String result;
    
    // 간결 양식 부분
    private String role;
    private String perform;

    private String qualification;
    private String publisher;
    private LocalDate issueDate;
    private String simpleDescription;

    private List<String> keywords;
    private String warrant;
    private List<String> files;

    /**
     Experience 엔티티를 받아와서 Dto형식으로 변환
     필드가 null값이 들어가면 `JsonInclude.Include.NON_NULL`설정을 통해 응답으로 무시됨
     */
    public static DetailExperienceReadResponseDto of(Experience experience) {
        DetailExperienceReadResponseDto dto = null;
        if(IS_QUALIFICATION.contains(experience.getMetaData().getExperienceType())) {
            dto = DetailExperienceReadResponseDto.builder()
                    .id(experience.getId())
                    .status(experience.getMetaData().getStatus())
                    .formType(null)
                    .experienceType(experience.getMetaData().getExperienceType())
                    .isEnded(experience.getPeriod().getIsEnded())
                    .startDate(null)
                    .endDate(null)
                    .title(null)
                    .situation(null)
                    .task(null)
                    .action(null)
                    .result(null)
                    .role(null)
                    .perform(null)
                    .qualification(experience.getQualification().getQualification())
                    .publisher(experience.getQualification().getPublisher())
                    .issueDate(experience.getPeriod().getEndDate())
                    .simpleDescription(null)
                    .keywords(null)
                    .warrant(null)
                    .files(null)
                    .build();
        }
        else {
             DetailExperienceReadResponseDtoBuilder commonFields = getCommonFields(experience);
            switch (experience.getMetaData().getFormType()) {
                case STAR_FORM -> dto = commonFields
                            .situation(experience.getStarForm().getSituation())
                            .task(experience.getStarForm().getTask())
                            .action(experience.getStarForm().getAction())
                            .result(experience.getStarForm().getResult())
                            .role(null)
                            .perform(null)
                            .build();
                case SIMPLE_FORM -> dto = commonFields
                            .situation(null)
                            .task(null)
                            .action(null)
                            .result(null)
                            .role(experience.getSimpleForm().getRole())
                            .perform(experience.getSimpleForm().getPerform())
                            .build();
            }
        }
        return dto;
    }

    private static DetailExperienceReadResponseDtoBuilder getCommonFields(Experience experience) {
        return DetailExperienceReadResponseDto.builder()
                .id(experience.getId())
                .status(experience.getMetaData().getStatus())
                .formType(experience.getMetaData().getFormType())
                .experienceType(experience.getMetaData().getExperienceType())
                .isEnded(experience.getPeriod().getIsEnded())
                .startDate(experience.getPeriod().getStartDate())
                .endDate(experience.getPeriod().getEndDate())
                .title(experience.getCommon().getTitle())
                .qualification(null)
                .publisher(null)
                .simpleDescription(null)
                .keywords(experience.getKeywords().stream().map(Keyword::getName).toList())
                .warrant(Experience.isNeedWarrant(experience.getMetaData().getExperienceType().toString())
                        ? experience.getCommon().getWarrant() : null)
                .files(Experience.isNeedFiles(experience.getMetaData().getExperienceType().toString())
                        ? experience.getFiles().stream().map(File::getFileUrl).collect(Collectors.toList()) : null);
    }
}
