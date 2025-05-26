package com.itstime.xpact.domain.dashboard.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Schema
public class SkillMapResponseDto {

    @Schema(example = """
            {
                    "coreSkillMaps": [
                        {
                            "coreSkillName": "프로젝트 관리",
                            "score": 9.5
                        },
                        {
                            "coreSkillName": "커뮤니케이션",
                            "score": 8.5
                        },
                        {
                            "coreSkillName": "문제 해결",
                            "score": 6.5
                        },
                        {
                            "coreSkillName": "리더십",
                            "score": 8.0
                        },
                        {
                            "coreSkillName": "전략적 사고",
                            "score": 7.0
                        }
                    ]
                }
            """)
    private List<ScoreDto> coreSkillMaps;
    @Schema(example = """
            "strengthFeedback": {
                        "weaknessName": "프로젝트 관리",
                        "reason": "프로젝트를 성공적으로 이끌어내고 결과물을 성공적으로 완성한 다수의 경험을 토대로 프로젝트 관리 능력이 두드러집니다.",
                        "careerSuggestion": "프로젝트 리더나 제품 관리자로서 프로젝트를 이끄는 역할이 잘 맞을 것 같습니다."
                    }
            """)
    private StrengthFeedback strengthFeedback;
    @Schema(example = """
            "weaknessFeedback": {
                        "strengthName": "문제 해결",
                        "reason": "긍정적인 성과를 이끌어내긴 했지만, 구체적인 문제 해결 과정에 대한 상세한 경험이 부족하게 보입니다.",
                        "improvementSuggestion": "특정 사항이나 과제를 해결하는 경험을 쌓아 보시는 것이 좋을 것 같습니다."
                    }
            """)
    private WeaknessFeedback weaknessFeedback;

    @Data
    public static class ScoreDto {
        private String coreSkillName;
        private double score;
    }
}
