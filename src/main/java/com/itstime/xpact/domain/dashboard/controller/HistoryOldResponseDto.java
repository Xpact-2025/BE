package com.itstime.xpact.domain.dashboard.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class HistoryOldResponseDto {

    private List<DateCount> dateCounts;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DateCount {
        private String date;
        private int count;
    }

    public static HistoryOldResponseDto of(List<DateCount> dateCounts) {
        return HistoryOldResponseDto.builder()
                .dateCounts(dateCounts)
                .build();
    }
}
