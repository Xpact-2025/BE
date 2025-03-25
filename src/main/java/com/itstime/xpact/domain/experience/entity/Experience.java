package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.recruit.entity.ExperienceKeyword;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "experience")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "form_type")
public abstract class Experience extends BaseEntity {

    @Id
    @Column(name = "experience_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    // TODO 경험 유형 enum이 아닌 테이블로 추가 고려
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExperienceType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "is_ended", nullable = false)
    private Boolean isEnded;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "experience", cascade = CascadeType.ALL)
    private List<ExperienceKeyword> expKeywords = new ArrayList<>();

    @OneToOne(mappedBy = "experience", cascade = CascadeType.ALL)
    private SummarizedExperience summarizedExperience;

}
