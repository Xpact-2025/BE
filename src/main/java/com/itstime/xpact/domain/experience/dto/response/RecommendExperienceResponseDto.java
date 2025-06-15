package com.itstime.xpact.domain.experience.dto.response;

import com.itstime.xpact.global.chroma.DocumentSplitter;
import lombok.Builder;
import lombok.Getter;
import org.springframework.ai.document.Document;

@Builder
@Getter
public class RecommendExperienceResponseDto {
    private Long id;
    private String title;
    private String linkPoint;

}
