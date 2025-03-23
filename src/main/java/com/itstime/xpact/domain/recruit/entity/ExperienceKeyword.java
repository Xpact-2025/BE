package com.itstime.xpact.domain.recruit.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.experience.entity.Experience;
import jakarta.persistence.*;

@Entity
@Table(name = "experience_keyword")
public class ExperienceKeyword extends BaseEntity {

    @Id
    @Column(name = "experience_keyword_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experience_id", nullable = false)
    private Experience experience;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;
}
