package com.itstime.xpact.domain.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class HistoryResponseDto {

    private Map<Integer, List<DateCount>> dateCounts;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DateCount {
        private String date;
        private int count;
    }

    public static HistoryResponseDto of(Map<Integer, List<DateCount>> groupByMonth) {
        return HistoryResponseDto.builder()
                .dateCounts(groupByMonth)
                .build();
    }
}
