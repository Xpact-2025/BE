package com.itstime.xpact.domain.dashboard.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@Schema(name = "RatioResponseDto", description = "직무별 비율 정보를 담은 응답 DTO")
public class RatioResponseDto {

    @Schema(
            description = "직무 이름과 해당 직무의 비율을 나타내는 Map 형식",
            example = """
                {
                  "백엔드": 66.7,
                  "프론트엔드": 33.3
                }
                """
    )
    private Map<String, Double> ratios;

    public static RatioResponseDto of(Map<String, Double> data) {
        return RatioResponseDto.builder()
                .ratios(data)
                .build();
    }
}
