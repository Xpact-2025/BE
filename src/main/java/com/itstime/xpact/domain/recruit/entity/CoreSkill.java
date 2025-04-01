package com.itstime.xpact.domain.recruit.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "core_skill")
public class CoreSkill extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "core_skill_id")
    private Long coreSkillId;

    @OneToOne(mappedBy = "coreSkill")
    private Recruit recruit;
}
