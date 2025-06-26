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
    @Column(name = "scrap_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "scrap_type")
    private ScrapType scrapType;

    @Column(name = "title")
    private String title; // 제목

    @Column(name = "organizer_name")
    private String organizerName; // 기관


    @Column(name = "start_date")
    private LocalDate startDate; // 공고 시작일
    @Column(name = "end_date")
    private LocalDate endDate; // 공고 종료일

    @Column(name = "job_category")
    private String jobCategory; // 우선적으로 jobCategory로 설정( 후에 detailRecruit로 변경 고민 ), 분야 (대외활동 - 활동분야 / 공모전 - 공모분야 / 교육 - 교육분야 / 인턴 - 모집분야)

    @Column(name = "homepage_url", length = 512)
    private String homepageUrl;

    @Column(name = "img_url")
    private String imgUrl;

    // 대외활동, 공모전에서만 사용하는 필드
    @Column(name = "benefits")
    private String benefits;
    @Column(name = "eligibility")
    private String eligibility;

    // 교육에서만 사용하는 필드
    @Column(name = "on_off_line")
    private String onOffLine;

    // 인턴에서만 사용하는 필드
    @Column(name = "enterprise_type")
    private String enterpriseType;
    @Column(name = "region")
    private String region;


//    @Embedded
//    private ScrapIntern scrapIntern;
//
//    @Embedded
//    private ScrapCompetition scrapCompetition;
//
//    @Embedded
//    private ScrapEducation scrapEducation;
//
//    @Embedded
//    private ScrapActivity scrapActivity;
//
//    @OneToMany(mappedBy = "scrap")
//    private List<MemberScrap> MemberScrapList;
//
//    public void validateScrapType() {
//        switch (this.scrapType) {
//            case INTERN -> {
//                if (scrapIntern == null) {
//                    throw GeneralException.of(ErrorCode.TEST);
//                }
//            } case COMPETITION -> {
//                if (scrapCompetition == null) {
//                    throw GeneralException.of(ErrorCode.TEST);
//                }
//            } case EDUCATION -> {
//                if (scrapEducation == null) {
//                    throw GeneralException.of(ErrorCode.TEST);
//                }
//            } case ACTIVITY -> {
//                if (scrapActivity == null) {
//                    throw GeneralException.of(ErrorCode.TEST);
//                }
//            }
//        }
//    }
}
