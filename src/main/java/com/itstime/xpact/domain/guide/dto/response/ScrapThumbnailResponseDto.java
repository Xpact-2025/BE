package com.itstime.xpact.domain.guide.dto.response;

import com.itstime.xpact.domain.guide.common.ScrapType;
import com.itstime.xpact.domain.guide.entity.Scrap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapThumbnailResponseDto {

    private Long id;
    private String title;
    private String imgUrl;
    private String dDay;
    private ScrapType scrapType;
    private Boolean isCliped;

    public static ScrapThumbnailResponseDto of(Scrap scrap, Boolean isCliped) {
        String dDay = null;

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            LocalDate endDate = LocalDate.parse(scrap.getEndDate(), formatter);
            dDay = String.valueOf(-ChronoUnit.DAYS.between(LocalDate.now(), endDate));
        } catch (DateTimeParseException ignored) { // endDate에 '채용 마감 시'같은 date가 아닌 string이 있을 때
            dDay = scrap.getEndDate();
        }

        return ScrapThumbnailResponseDto.builder()
                .id(scrap.getId())
                .title(scrap.getTitle())
                .imgUrl(scrap.getImgUrl())
                .dDay(dDay == null ? String.valueOf(dDay) : dDay)
                .scrapType(scrap.getScrapType())
                .isCliped(isCliped)
                .build();
    }
}
