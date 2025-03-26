package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
public class Category extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ExperienceCategory> experienceCategories = new ArrayList<>();
}
