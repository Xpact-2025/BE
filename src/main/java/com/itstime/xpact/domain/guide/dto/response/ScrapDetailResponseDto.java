package com.itstime.xpact.domain.guide.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itstime.xpact.domain.guide.common.ScrapType;
import com.itstime.xpact.domain.guide.entity.Scrap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScrapDetailResponseDto {

    private Long scrapId;
    private ScrapType scrapType;
    private String title;
    private String organization;
    private String imageUrl;
    private String referenceUrl;
    private List<String> jobCategory;
    private String startDate;
    private String endDate;

    private String eligibility;
    private String benefits;

    private String onOffLine;

    private String enterpriseType;
    private String region;

    public static ScrapDetailResponseDto of(Scrap scrap, List<String> jobCategory) {
        return ScrapDetailResponseDto.builder()
                .scrapId(scrap.getId())
                .scrapType(scrap.getScrapType())
                .title(scrap.getTitle())
                .organization(scrap.getOrganizerName())
                .imageUrl(scrap.getImgUrl())
                .referenceUrl(scrap.getHomepageUrl())
                .jobCategory(jobCategory)
                .startDate(scrap.getStartDate())
                .endDate(scrap.getEndDate())
                .eligibility(scrap.getEligibility())
                .benefits(scrap.getBenefits())
                .onOffLine(scrap.getOnOffLine())
                .enterpriseType(scrap.getEnterpriseType())
                .region(scrap.getRegion())
                .build();
    }
}
