package com.itstime.xpact.domain.recruit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DetailRecruitDto {

    @JsonProperty("recruit")
    private String recruitName;

    @JsonProperty("detailRecruit")
    private String detailRecruitName;

    public static DetailRecruitDto of(String recruitName, String detailRecruitName) {
        return DetailRecruitDto.builder()
                .recruitName(recruitName)
                .detailRecruitName(detailRecruitName)
                .build();
    }
}
