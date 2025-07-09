package com.itstime.xpact.domain.guide.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.guide.common.ScrapType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;


@Entity
@Table(name = "scrap")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@BatchSize(size = 20)
public class Scrap extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long id;

    @Column(name = "linkareer_id", unique = true)
    private Long linkareerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "scrap_type")
    private ScrapType scrapType;

    @Column(name = "title")
    private String title; // 제목

    @Column(name = "organizer_name")
    private String organizerName; // 기관


    @Column(name = "start_date")
    private String startDate; // 공고 시작일
    @Column(name = "end_date")
    private String endDate; // 공고 종료일

    @Column(name = "job_category")
    private String jobCategory; // 우선적으로 jobCategory로 설정( 후에 detailRecruit로 변경 고민 ), 분야 (대외활동 - 활동분야 / 공모전 - 공모분야 / 교육 - 교육분야 / 인턴 - 모집분야)

    @Column(name = "homepage_url", length = 512)
    private String homepageUrl;

    @Column(name = "img_url")
    private String imgUrl;

    // 대외활동, 공모전에서만 사용하는 필드
    @Column(name = "benefits")
    private String benefits; // 활동혜택
    @Column(name = "eligibility")
    private String eligibility; // 지원자격

    // 교육에서만 사용하는 필드
    @Column(name = "on_off_line")
    private String onOffLine; // 온/오프라인

    // 인턴에서만 사용하는 필드
    @Column(name = "enterprise_type")
    private String enterpriseType; // 기업형태
    @Column(name = "region")
    private String region; // 근무지역
}
