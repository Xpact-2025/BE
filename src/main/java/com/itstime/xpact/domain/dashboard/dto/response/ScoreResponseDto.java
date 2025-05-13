package com.itstime.xpact.domain.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@AllArgsConstructor
@Data
public class ScoreResponseDto {

    private String coreSkillName;
    private double score;
}
