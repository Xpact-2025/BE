package com.itstime.xpact.domain.experience.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.entity.Experience;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThumbnailExperienceReadResponseDto {
    private Long id;
    private String title;
    private List<String> experienceCategories;

    public static ThumbnailExperienceReadResponseDto of(Experience experience, List<String> experienceCategoryNames) {
        return ThumbnailExperienceReadResponseDto.builder()
                .id(experience.getId())
                .title(experience.getTitle())
                .experienceCategories(experienceCategoryNames)
                .build();
    }
}
