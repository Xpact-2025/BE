package com.itstime.xpact.domain.experience.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.entity.Experience;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThumbnailExperienceReadResponseDto {
    private Long id;
    private String title;

    // TODO category 추가해야함
    public static ThumbnailExperienceReadResponseDto of(Experience experience) {
        return ThumbnailExperienceReadResponseDto.builder()
                .id(experience.getId())
                .title(experience.getTitle())
                .build();
    }
}
