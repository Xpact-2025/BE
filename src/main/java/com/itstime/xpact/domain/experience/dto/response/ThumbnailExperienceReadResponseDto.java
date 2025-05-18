package com.itstime.xpact.domain.experience.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.entity.Experience;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThumbnailExperienceReadResponseDto {
    private Long id;
    private String title;
    private ExperienceType experienceType;
    private Status status;

    public static ThumbnailExperienceReadResponseDto of(Experience experience) {
        return ThumbnailExperienceReadResponseDto.builder()
                .id(experience.getId())
                .title(experience.getTitle() != null ? experience.getTitle() : experience.getQualification().getQualification())
                .experienceType(experience.getMetaData().getExperienceType())
                .status(experience.getMetaData().getStatus())
                .build();
    }
}
