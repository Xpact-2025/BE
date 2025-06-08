package com.itstime.xpact.domain.guide.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.guide.dto.WeaknessGuideResponseDto;
import com.itstime.xpact.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "weakness")
@RequiredArgsConstructor
public class Weakness extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weakness_id")
    private long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "name")
    @Setter
    private String name;

    @Setter
    @Column(name = "explanation")
    private String explanation;

    public static WeaknessGuideResponseDto toDto(Weakness weakness) {
        return WeaknessGuideResponseDto
                .builder()
                .weaknessName(weakness.getName())
                .explanation(weakness.getExplanation())
                .build();
    }

    public Weakness(Member member, String skillName, String explanation) {
        this.member = member;
        this.name = skillName;
        this.explanation = explanation;
    }
}
