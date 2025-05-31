package com.itstime.xpact.domain.dashboard.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema
public class StrengthFeedback {
    @Schema(example = "핵심 역량 중 강점 역량의 이름")
    private String strengthName;
    @Schema(example = "강점 분석 이유")
    private String reason;
    @Schema(example = "추천하는 커리어")
    private String careerSuggestion;
}
