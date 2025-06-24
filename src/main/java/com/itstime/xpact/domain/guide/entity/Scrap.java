package com.itstime.xpact.domain.guide.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.guide.common.ScrapType;
import com.itstime.xpact.domain.guide.entity.embeddable.ScrapActivity;
import com.itstime.xpact.domain.guide.entity.embeddable.ScrapCompetition;
import com.itstime.xpact.domain.guide.entity.embeddable.ScrapEducation;
import com.itstime.xpact.domain.guide.entity.embeddable.ScrapIntern;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.exception.GeneralException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "scrap")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Scrap extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_activity_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "scrap_type")
    private ScrapType scrapType;

    @Column(name = "title")
    private String title;

    @Column(name = "organizer_name")
    private String organizerName;

    @Column(name = "d_day")
    private int dDay;

    @Column(name = "work_type")
    private String workType;

    @Column(name = "region")
    private String region;

    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "job_category")
    private String jobCategory; // 우선적으로 jobCategory로 설정( 후에 detailRecruit로 변경 고민 )

    @Column(name = "homepage_url")
    private String homepageUrl;

    @Column(name = "img_url")
    private String imgUrl;

    // 부분 일치
    @Column(name = "benefits")
    private String benefits;
    @Column(name = "additional_benefits")
    private String additionalBenefits;
    @Column(name = "period")
    private String period;
    @Column(name = "recruit_number")
    private String recruitNumber;

    @Embedded
    private ScrapIntern scrapIntern;

    @Embedded
    private ScrapCompetition scrapCompetition;

    @Embedded
    private ScrapEducation scrapEducation;

    @Embedded
    private ScrapActivity scrapActivity;

    @OneToMany(mappedBy = "scrap")
    private List<MemberScrap> MemberScrapList;

    public void validateScrapType() {
        switch (this.scrapType) {
            case INTERN -> {
                if (scrapIntern == null) {
                    throw GeneralException.of(ErrorCode.TEST);
                }
            } case COMPETITION -> {
                if (scrapCompetition == null) {
                    throw GeneralException.of(ErrorCode.TEST);
                }
            } case EDUCATION -> {
                if (scrapEducation == null) {
                    throw GeneralException.of(ErrorCode.TEST);
                }
            } case ACTIVITY -> {
                if (scrapActivity == null) {
                    throw GeneralException.of(ErrorCode.TEST);
                }
            }
        }
    }
}
