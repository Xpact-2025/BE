package com.itstime.xpact.domain.dashboard.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private String name;
    private Double value;

    public static RatioResponseDto of(Map.Entry<String, Double> entry) {
        return RatioResponseDto.builder().name(entry.getKey()).value(entry.getValue()).build();
    }
}
