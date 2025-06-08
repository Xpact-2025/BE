package com.itstime.xpact.domain.guide.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.guide.common.ScrapType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Table(name = "scrap")
@Getter
public class Scrap extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ScrapType scrapType;

    private String title;

    private String speculation;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private String detailRecruit;

    private String coreSkill;

    private String enterprise;

    @Column(name = "img_url")
    private String imgUrl;

    private String region;
}
