package com.itstime.xpact.domain.guide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itstime.xpact.domain.guide.common.ScrapType;
import com.itstime.xpact.domain.guide.entity.Scrap;
import lombok.Getter;

@Getter
public class ScrapRequestDto {

    @JsonProperty("linkareer_id")
    private Long linkareerId;
    @JsonProperty("scrap_type")
    private ScrapType scrapType;
    @JsonProperty("title")
    private String title;
    @JsonProperty("orgnizer_name")
    private String organizerName;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("job_category")
    private String jobCategory;
    @JsonProperty("homepage_url")
    private String homepageUrl;
    @JsonProperty("img_url")
    private String imgUrl;
    @JsonProperty("benefits")
    private String benefits;
    @JsonProperty("eligibility")
    private String eligibility;
    @JsonProperty("on_off_line")
    private String onOffline;
    @JsonProperty("enterprise_type")
    private String enterpriseType;
    @JsonProperty("region")
    private String region;

    public Scrap toEntity(ScrapType scrapType) {
        return Scrap.builder()
                .linkareerId(this.linkareerId)
                .title(this.title)
                .organizerName(this.organizerName)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .jobCategory(this.jobCategory)
                .homepageUrl(this.homepageUrl)
                .imgUrl(this.imgUrl)
                .benefits(this.benefits)
                .eligibility(this.eligibility)
                .onOffLine(this.onOffline)
                .enterpriseType(this.enterpriseType)
                .region(this.region)
                .scrapType(scrapType)
                .build();
    }
}
