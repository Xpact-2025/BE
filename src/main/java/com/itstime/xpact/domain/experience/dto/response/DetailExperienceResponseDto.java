package com.itstime.xpact.domain.experience.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.FileType;
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
public class DetailExperienceResponseDto {

    // 공통 부분
    private Long experienceId;
    private ExperienceType experienceType;
    private Status status;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;

    private String qualification;
    private String publisher;
    private LocalDate issueDate;

    List<SubExperienceResponseDto> subExperiencesResponseDto;

    @Getter
    @Builder
    public static class SubExperienceResponseDto {
        private Long subExperienceId;
        private FormType formType;
        private String tabName;
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
        private List<String> links;
        private List<String> keywords;
    }


    /**
     Experience 엔티티를 받아와서 Dto형식으로 변환
     필드가 null값이 들어가면 `JsonInclude.Include.NON_NULL`설정을 통해 응답으로 무시됨
     */
    public static DetailExperienceResponseDto of(List<SubExperience> subExperiences, Experience experience) {
        DetailExperienceResponseDtoBuilder dto;
        if (IS_QUALIFICATION.contains(experience.getExperienceType())) {
            dto = DetailExperienceResponseDto.builder()
                    .experienceId(experience.getId())
                    .experienceType(experience.getExperienceType())
                    .status(experience.getStatus())
                    .qualification(experience.getQualification())
                    .publisher(experience.getPublisher())
                    .issueDate(experience.getEndDate());
        } else {
            dto = DetailExperienceResponseDto.builder()
                    .experienceId(experience.getId())
                    .experienceType(experience.getExperienceType())
                    .status(experience.getStatus())
                    .title(experience.getTitle())
                    .startDate(experience.getStartDate())
                    .endDate(experience.getEndDate());
        }

        List<SubExperienceResponseDto> subExperienceResponseDtos = subExperiences.stream()
                .map(subExperience -> {
                    SubExperienceResponseDto subExperienceResponseDto = null;
                    if(IS_QUALIFICATION.contains(experience.getExperienceType())) {
                        subExperienceResponseDto = SubExperienceResponseDto.builder()
                                .subExperienceId(subExperience.getId())
                                .formType(subExperience.getFormType())
                                .tabName(subExperience.getTabName())
                                .subTitle(subExperience.getSubTitle())
                                .simpleDescription(subExperience.getSimpleDescription())
                                .keywords(subExperience.getKeywords().stream().map(Keyword::getName).collect(Collectors.toList()))
                                .build();
                    }
                    else {
                        switch (subExperience.getFormType()) {
                            case STAR_FORM -> subExperienceResponseDto = SubExperienceResponseDto.builder()
                                    .subExperienceId(subExperience.getId())
                                    .formType(subExperience.getFormType())
                                    .tabName(subExperience.getTabName())
                                    .subTitle(subExperience.getSubTitle())
                                    .situation(subExperience.getStarForm().getSituation())
                                    .task(subExperience.getStarForm().getTask())
                                    .action(subExperience.getStarForm().getAction())
                                    .result(subExperience.getStarForm().getResult())
                                    .keywords(subExperience.getKeywords().stream().map(Keyword::getName).collect(Collectors.toList()))
                                    .files(subExperience.getFiles().stream()
                                            .filter(file -> file.getFileType().equals(FileType.FILE))
                                            .map(File::getFileUrl).collect(Collectors.toList()))
                                    .links(subExperience.getFiles().stream()
                                            .filter(file -> file.getFileType().equals(FileType.LINK))
                                            .map(File::getFileUrl).collect(Collectors.toList()))
                                    .build();

                            case SIMPLE_FORM -> subExperienceResponseDto = SubExperienceResponseDto.builder()
                                    .subExperienceId(subExperience.getId())
                                    .formType(subExperience.getFormType())
                                    .tabName(subExperience.getTabName())
                                    .subTitle(subExperience.getSubTitle())
                                    .role(subExperience.getSimpleForm().getRole())
                                    .perform(subExperience.getSimpleForm().getPerform())
                                    .keywords(subExperience.getKeywords().stream().map(Keyword::getName).collect(Collectors.toList()))
                                    .files(subExperience.getFiles().stream()
                                            .filter(file -> file.getFileType().equals(FileType.FILE))
                                            .map(File::getFileUrl).collect(Collectors.toList()))
                                    .links(subExperience.getFiles().stream()
                                            .filter(file -> file.getFileType().equals(FileType.LINK))
                                            .map(File::getFileUrl).collect(Collectors.toList()))
                                    .build();
                        }
                    }
                    return subExperienceResponseDto;
                }).toList();

        dto.subExperiencesResponseDto(subExperienceResponseDtos).build();
        return dto.build();
    }
}
