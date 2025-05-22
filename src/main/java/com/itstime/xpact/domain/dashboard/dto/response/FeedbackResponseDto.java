package com.itstime.xpact.domain.dashboard.dto.response;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDto {
    private String expAnalysis;
    private String recommend;
}
