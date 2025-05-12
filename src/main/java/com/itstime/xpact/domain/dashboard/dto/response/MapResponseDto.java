package com.itstime.xpact.domain.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class MapResponseDto {
    private List<ScoreResponseDto> coreSkillMaps;

}
