package com.itstime.xpact.domain.dashboard.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StrengthFeedback {
    @Schema(name = "핵심 역량 중 강점 역량의 이름", example = "프로젝트 관리")
    private String strengthName;
    @Schema(name = "강점 분석 이유", example = "유저")
    private String reason;
    private String careerSuggestion;
}
