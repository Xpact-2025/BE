package com.itstime.xpact.domain.recruit.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema
public record DesiredRecruitResponseDto (
    @Schema(description = "희망 산업 분야",
            example = "법률·법무·회계")
    String recruitName,

    @Schema(description = "희망 상세 직군 분야",
            example = "법률사무원")
    String detailRecruitName
) {
}