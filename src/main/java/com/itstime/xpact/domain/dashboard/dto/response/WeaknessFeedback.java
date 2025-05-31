package com.itstime.xpact.domain.dashboard.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema
public class WeaknessFeedback {
    @Schema(example = "핵심 역량 중 약점 역량의 이름")
    private String weaknessName;
    @Schema(example = "약점 분석 이유")
    private String reason;
    @Schema(example = "보완해야할 점")
    private String improvementSuggestion;
}
