package com.itstime.xpact.domain.recruit.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.experience.entity.Experience;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @OneToOne(mappedBy = "detailRecruit")
    private Experience experience;

    @Builder
    public DetailRecruit(Recruit recruit, String name) {
        this.recruit = recruit;
        this.name = name;
    }
}
