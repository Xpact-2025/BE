package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "experience")
@SuperBuilder
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "form_type")
public abstract class Experience extends BaseEntity {

    @Id
    @Column(name = "experience_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    protected Status status;

    @Column(name = "title", nullable = false)
    protected String title;

    @Column(name = "is_ended", nullable = false)
    protected Boolean isEnded;

    @Column(name = "start_date")
    protected LocalDate startDate;

    @Column(name = "end_date")
    protected LocalDate endDate;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    protected ExperienceType type;

    @Column(name = "summary")
    protected String summary;

    @Column(name = "keyword")
    protected String keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_recruit_id")
    private DetailRecruit detailRecruit;

    public abstract void update(ExperienceUpdateRequestDto dto);

    public void addMember(Member member) {
        this.member = member;
        member.getExperiences().add(this);
    }
}
