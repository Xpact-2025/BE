package com.itstime.xpact.domain.experience.dto;

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

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailExperienceReadResponseDto {

    // 공통 부분
    private Long id;
    private Status status;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private FormType formType;
    private ExperienceType experienceType;

    // STAR 양식 부분
    private String situation;

    private String task;
    private String action;
    private String result;
    // 간결 양식 부분
    private String role;
    private String perform;

    /**
     Experience 엔티티를 받아와서 Dto형식으로 변환
     이때 StarForm, SimpleForm이냐에 따라 형식이 바뀌므로 조건문을 통해 리턴을 다르게 함
     필드가 null값이 들어가면 `JsonInclude.Include.NON_NULL`설정을 통해 응답으로 무시됨
     */
    public static DetailExperienceReadResponseDto from(Experience experience) {

        if(experience instanceof StarForm starForm) {
            return DetailExperienceReadResponseDto.builder()
                    .id(starForm.getId())
                    .status(starForm.getStatus())
                    .title(starForm.getTitle())
                    .startDate(starForm.getStartDate())
                    .endDate(starForm.getEndDate())
                    .formType(FormType.STAR_FORM)
                    .experienceType(starForm.getType())

                    .situation(starForm.getSituation())
                    .task(starForm.getTask())
                    .action(starForm.getAction())
                    .result(starForm.getResult())
                    .build();
        } else if(experience instanceof SimpleForm simpleForm) {
            return DetailExperienceReadResponseDto.builder()
                    .id(simpleForm.getId())
                    .status(simpleForm.getStatus())
                    .title(simpleForm.getTitle())
                    .startDate(simpleForm.getStartDate())
                    .endDate(simpleForm.getEndDate())
                    .formType(FormType.SIMPLE_FORM)
                    .experienceType(simpleForm.getType())

                    .role(simpleForm.getRole())
                    .perform(simpleForm.getPerform())
                    .build();
        } else {
            throw CustomException.of(ErrorCode.INVALID_FORMTYPE);
        }
    }
}
