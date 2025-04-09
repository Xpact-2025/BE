package com.itstime.xpact.domain.recruit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DetailRecruitDto {
    private String recruitName;
    private String detailRecruitName;

    public static DetailRecruitDto of(String recruitName, String detailRecruitName) {
        return DetailRecruitDto.builder()
                .recruitName(recruitName)
                .detailRecruitName(detailRecruitName)
                .build();
    }
}
