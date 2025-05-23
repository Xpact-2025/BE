package com.itstime.xpact.domain.dashboard.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "핵심역량에 대한 피드백 응답에 대한 DTO")
public class FeedbackResponseDto {
    @Schema(description = "사용자의 강점 및 약점 역량",
    example = "사용자 중심 사고")
    private String coreSkillName;
    @Schema(description = "경험 기반 역량에 대한 분석 이유를 설명",
    example = "뉴스 앱 기획 인턴 경험에서, 사용자 피드백 기반으로 UI 개선안을 도출하고, 사용성 중심의 기획 방향을 제안한 점이 주요하게 반영되었습니다.")
    private String expAnalysis;
    @Schema(description = "커리어 연결 및 추천 활동",
    example = "UX 기획, 프로덕트 매니지먼트 직무에서 큰 강점으로 작용할 수 있으며, 사용자 인터뷰 및 프로토타입 개선 과정에서의 리더십을 더욱 강화해보세요.")
    private String recommend;
}
