package com.itstime.xpact.domain.guide.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WeaknessGuideResponseDto {
        private String weaknessName;
        private String explanation;
}
