package com.itstime.xpact.domain.guide.dto;

import com.itstime.xpact.domain.guide.common.ScrapType;
import com.itstime.xpact.domain.guide.entity.Scrap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapThumbnailResponseDto {

    private Long id;
    private String title;
    private String weakness;
    private String imgUrl;
    private Integer dDay;
    private ScrapType scrapType;
    private Boolean isCliped;

    public static ScrapThumbnailResponseDto of(Scrap scrap, Boolean isCliped) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate endDate = LocalDate.parse(scrap.getEndDate(), formatter);
        long dDay = -ChronoUnit.DAYS.between(LocalDate.now(), endDate);

        return ScrapThumbnailResponseDto.builder()
                .id(scrap.getId())
                .title(scrap.getTitle())
                .imgUrl(scrap.getImgUrl())
                .dDay((int) dDay)
                .scrapType(scrap.getScrapType())
                .isCliped(isCliped)
                .build();
    }

}
