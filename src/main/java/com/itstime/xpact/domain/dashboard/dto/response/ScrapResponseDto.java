package com.itstime.xpact.domain.dashboard.dto.response;

import com.itstime.xpact.domain.guide.entity.Scrap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapResponseDto {
    private Long scrapId;
    private String title;
    private String dDay;

    public static ScrapResponseDto of(Scrap scrap, String dDay) {
        return ScrapResponseDto.builder()
                .scrapId(scrap.getId())
                .title(scrap.getTitle())
                .dDay(dDay)
                .build();
    }
}
