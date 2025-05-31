package com.itstime.xpact.domain.dashboard.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeaknessFeedback {
    private String weaknessName;
    private String reason;
    private String improvementSuggestion;
}
