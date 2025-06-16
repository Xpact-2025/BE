package com.itstime.xpact.domain.resume.entity.embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendExperience {
    private Long id;
    private String title;
    private String linkPoint;
}
