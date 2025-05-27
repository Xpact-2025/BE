package com.itstime.xpact.domain.experience.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.entity.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.itstime.xpact.domain.experience.common.ExperienceType.IS_QUALIFICATION;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailExperienceReadResponseDto {

    // 공통 부분
    private Long groupId;
    private ExperienceType experienceType;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;

    private String qualification;
    private String publisher;
    private LocalDate issueDate;

    List<SubExperienceResponseDto> subExps;

    @Getter
    @Builder
    public static class SubExperienceResponseDto {
        private FormType formType;
        private Status status;
        private String subTitle;

        // STAR 양식 부분
        private String situation;
        private String task;
        private String action;
        private String result;

        // 간결 양식 부분
        private String role;
        private String perform;

        private String simpleDescription;

        private List<String> files;
        private List<String> keywords;
    }


    /**
     Experience 엔티티를 받아와서 Dto형식으로 변환
     필드가 null값이 들어가면 `JsonInclude.Include.NON_NULL`설정을 통해 응답으로 무시됨
     */
    public static DetailExperienceReadResponseDto of(List<Experience> experiences, Long groupId) {
        Experience commonExperience = experiences.get(0);
        DetailExperienceReadResponseDtoBuilder dto;
        if (IS_QUALIFICATION.contains(commonExperience.getExperienceType())) {
            dto = DetailExperienceReadResponseDto.builder()
                    .groupId(groupId)
                    .experienceType(commonExperience.getExperienceType())
                    .qualification(commonExperience.getQualification())
                    .publisher(commonExperience.getPublisher())
                    .issueDate(commonExperience.getEndDate());
        } else {
            dto = DetailExperienceReadResponseDto.builder()
                    .groupId(groupId)
                    .experienceType(commonExperience.getExperienceType())
                    .title(commonExperience.getTitle())
                    .startDate(commonExperience.getStartDate())
                    .endDate(commonExperience.getEndDate());
        }

        List<SubExperienceResponseDto> subExperienceResponseDtos = experiences.stream()
                .map(experience -> {
                    SubExperienceResponseDto subExperienceResponseDto = null;
                    if(IS_QUALIFICATION.contains(experience.getExperienceType())) {
                        subExperienceResponseDto = SubExperienceResponseDto.builder()
                                .formType(experience.getFormType())
                                .status(experience.getStatus())
                                .subTitle(experience.getSubTitle())
                                .simpleDescription(experience.getSimpleDescription())
                                .keywords(experience.getKeywords().stream().map(Keyword::getName).collect(Collectors.toList()))
                                .build();
                    }
                    else {
                        switch (experience.getFormType()) {
                            case STAR_FORM -> subExperienceResponseDto = SubExperienceResponseDto.builder()
                                    .formType(experience.getFormType())
                                    .status(experience.getStatus())
                                    .subTitle(experience.getSubTitle())
                                    .situation(experience.getStarForm().getSituation())
                                    .task(experience.getStarForm().getTask())
                                    .action(experience.getStarForm().getAction())
                                    .result(experience.getStarForm().getResult())
                                    .keywords(experience.getKeywords().stream().map(Keyword::getName).collect(Collectors.toList()))
                                    .files(experience.getFiles().stream().map(File::getFileUrl).collect(Collectors.toList()))
                                    .build();

                            case SIMPLE_FORM -> subExperienceResponseDto = SubExperienceResponseDto.builder()
                                    .formType(experience.getFormType())
                                    .status(experience.getStatus())
                                    .subTitle(experience.getSubTitle())
                                    .role(experience.getSimpleForm().getRole())
                                    .perform(experience.getSimpleForm().getPerform())
                                    .keywords(experience.getKeywords().stream().map(Keyword::getName).collect(Collectors.toList()))
                                    .files(experience.getFiles().stream().map(File::getFileUrl).collect(Collectors.toList()))
                                    .build();
                        }
                    }
                    return subExperienceResponseDto;
                }).toList();

        dto.subExps(subExperienceResponseDtos).build();
        return dto.build();
    }
}
