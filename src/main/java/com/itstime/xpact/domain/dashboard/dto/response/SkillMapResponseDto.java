package com.itstime.xpact.domain.dashboard.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Schema
public class SkillMapResponseDto {

    @Schema
    private List<ScoreDto> coreSkillMaps;
    private StrengthFeedback strengthFeedback;
    private WeaknessFeedback weaknessFeedback;

    @Data
    @Schema
    public static class ScoreDto {
        @Schema(example = "직무에 따른 핵심역량 1~5")
        private String coreSkillName;
        @Schema(example = "핵심역량에 대한 점수")
        private double score;
    }
}
