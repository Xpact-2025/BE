package com.itstime.xpact.domain.experience.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.entity.Keyword;
import com.itstime.xpact.domain.experience.entity.SubExperience;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThumbnailExperienceWithkeywordResponseDto {
    private Long id;
    private String title;
    private ExperienceType experienceType;
    private LocalDateTime draftTime;
    private Status status;
    private List<String> keywords;
    private List<String> subTitles;

    public static ThumbnailExperienceWithkeywordResponseDto of(Experience experience) {
        return ThumbnailExperienceWithkeywordResponseDto.builder()
                .id(experience.getId())
                .title(experience.getTitle() != null ? experience.getTitle() : experience.getQualification())
                .experienceType(experience.getExperienceType())
                .draftTime(experience.getStatus().equals(Status.DRAFT) ? experience.getModifiedTime() : null)
                .status(experience.getStatus())
                .keywords(experience.getSubExperiences().stream()
                        .flatMap(subExperience -> subExperience.getKeywords().stream())
                        .map(Keyword::getName)
                        .collect(Collectors.toSet())
                        .stream().toList())
                .subTitles(experience.getSubExperiences().stream()
                        .map(SubExperience::getSubTitle)
                        .toList())
                .build();
    }
}
