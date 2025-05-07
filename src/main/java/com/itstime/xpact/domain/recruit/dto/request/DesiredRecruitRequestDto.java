package com.itstime.xpact.domain.recruit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원이 희망 직무를 저장할 때 사용하는 DTO")
public record DesiredRecruitRequestDto(
        @Schema(description = "희망 산업 분야",
        example = "법률·법무·회계")
        String recruitName,

        @Schema(description = "희망 상세 직군 분야",
        example = "법률사무원")
        String detailRecruitName
) {
}
