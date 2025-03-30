package com.itstime.xpact.domain.recruit.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.experience.entity.Experience;
import jakarta.persistence.*;

@Entity
@Table(name = "detail_recruit")
public class DetailRecruit extends BaseEntity {


    @Id
    @Column(name = "detail_recruit_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id", nullable = false)
    private Recruit recruit;

    @OneToOne(mappedBy = "detailRecruit")
    private Experience experience;
}
