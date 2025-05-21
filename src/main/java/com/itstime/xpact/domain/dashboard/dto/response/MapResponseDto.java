package com.itstime.xpact.domain.dashboard.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Schema(description = "핵심스킬맵 응답 DTO")
public class MapResponseDto {
    @Schema(description = "핵심스킬 이름과 그에 대한 점수",
    example = """
           [
           {
                            "coreSkillName": "프로젝트 관리",
                            "score": 8.5
                        },
                        {
                            "coreSkillName": "커뮤니케이션",
                            "score": 9.0
                        },
                        {
                            "coreSkillName": "문제 해결",
                            "score": 9.5
                        },
                        {
                            "coreSkillName": "리더십",
                            "score": 8.0
                        },
                        {
                            "coreSkillName": "전략적 사고",
                            "score": 9.0
                        }
            ]
            """)
    private List<ScoreResponseDto> coreSkillMaps;
    private String strength;
    private String weakness;

    public void setAnalysis(String strength, String weakness) {
        this.strength = strength;
        this.weakness = weakness;
    }
}
