package com.itstime.xpact.domain.recruit.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "recruit")
public class Recruit extends BaseEntity {

    @Id
    @Column(name = "recruit_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL)
    private List<Keyword> keywords;

    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL)
    private List<DesiredRecruit> desiredRecruits;
}
