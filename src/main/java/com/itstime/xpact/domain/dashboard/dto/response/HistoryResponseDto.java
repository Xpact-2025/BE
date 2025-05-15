package com.itstime.xpact.domain.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class HistoryResponseDto {

    private List<DateCount> dateCounts;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DateCount {
        private String date;
        private int count;
    }

    public static HistoryResponseDto of(List<DateCount> dateCounts) {
        return HistoryResponseDto.builder()
                .dateCounts(dateCounts)
                .build();
    }
}
