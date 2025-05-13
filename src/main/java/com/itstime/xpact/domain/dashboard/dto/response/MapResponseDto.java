package com.itstime.xpact.domain.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class MapResponseDto {
    private List<ScoreResponseDto> coreSkillMaps;

}
