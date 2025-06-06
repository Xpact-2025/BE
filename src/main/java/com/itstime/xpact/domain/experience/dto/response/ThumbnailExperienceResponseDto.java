package com.itstime.xpact.domain.experience.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.entity.Experience;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThumbnailExperienceResponseDto {
    private Long id;
    private String title;
    private ExperienceType experienceType;
    private LocalDateTime draftTime;
    private Status status;
    private List<String> keywords;

    public static ThumbnailExperienceResponseDto of(Experience experience) {
        return ThumbnailExperienceResponseDto.builder()
                .id(experience.getId())
                .title(experience.getTitle() != null ? experience.getTitle() : experience.getQualification())
                .experienceType(experience.getExperienceType())
                .draftTime(experience.getStatus().equals(Status.DRAFT) ? experience.getModifiedTime() : null)
                .status(experience.getStatus())
                .build();
    }
}
