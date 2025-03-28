package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@Table(name = "experience_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ExperienceCategory extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experience_category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experience_id", nullable = false)
    private Experience experience;

    public static ExperienceCategory from(Experience experience, Category category) {
        return ExperienceCategory.builder()
                .category(category)
                .experience(experience)
                .build();
    }
}
