package com.itstime.xpact.domain.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@AllArgsConstructor
public class ScoreResponseDto {

    private String coreSkillName;
    private double score;
}
