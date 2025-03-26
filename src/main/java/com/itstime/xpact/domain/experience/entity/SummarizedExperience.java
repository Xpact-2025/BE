package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "summarized_experience")
public class SummarizedExperience extends BaseEntity {

    @Id
    @Column(name = "summarized_experience_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experience_id")
    private Experience experience;

    @OneToMany(mappedBy = "summarizedExperience", cascade = CascadeType.ALL)
    private List<ExperienceCategory> experienceCategories = new ArrayList<>();
}
