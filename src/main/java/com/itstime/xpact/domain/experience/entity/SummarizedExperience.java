package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "summarized_experience")
public class SummarizedExperience extends BaseEntity {

    @Id
    @Column(name = "summarized_experience_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @OneToOne(mappedBy = "summaryExperience", cascade = CascadeType.ALL)
    private Experience experience;

}
