package com.itstime.xpact.domain.recruit.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "recruit")
public class Recruit extends BaseEntity {

    @Id
    @Column(name = "recruit_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL)
    private List<DetailRecruit> detailRecruits = new ArrayList<>();

    @OneToOne(mappedBy = "recruit")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "core_skill_id")
    private CoreSkill coreSkill;

    @Builder
    public Recruit(String name) {
        this.name = name;
    }
}
