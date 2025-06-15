package com.itstime.xpact.domain.experience.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecommendExperienceResponseDto {
    private Long id;
    private String title;
    private String linkPoint;

}
