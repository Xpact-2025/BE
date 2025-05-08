package com.itstime.xpact.domain.recruit.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "detail_recruit")
@NoArgsConstructor
@AllArgsConstructor
public class DetailRecruit extends BaseEntity {

    @Id
    @Column(name = "detail_recruit_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id", nullable = false)
    private Recruit recruit;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "core_skill_id")
    private CoreSkill coreSkill;

    @Builder
    public DetailRecruit(Recruit recruit, String name) {
        this.recruit = recruit;
        this.name = name;
    }
}
