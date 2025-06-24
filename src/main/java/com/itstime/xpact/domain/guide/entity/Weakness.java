package com.itstime.xpact.domain.guide.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.guide.dto.WeaknessGuideResponseDto;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.exception.GeneralException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;

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
    @Lob
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

    public Weakness updateWeakness(Member member, List<Weakness> weaknessList) {

        Weakness oldest = weaknessList.stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedTime))
                .findFirst()
                .orElseThrow(() -> GeneralException.of(ErrorCode.WEAKNESS_NOT_FOUND));

        oldest.setName(name);
        oldest.setExplanation(explanation);
        return oldest;
    }
}
