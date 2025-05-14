package com.itstime.xpact.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class RatioResponseDto {
    private Map<String, Double> ratios;

    public static RatioResponseDto of(Map<String, Double> data) {
        return RatioResponseDto.builder()
                .ratios(data)
                .build();
    }
}
