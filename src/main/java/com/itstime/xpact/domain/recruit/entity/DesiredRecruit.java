package com.itstime.xpact.domain.recruit.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.member.entity.Member;
import jakarta.persistence.*;

@Entity
@Table(name = "desired_recruit")
public class DesiredRecruit extends BaseEntity {


    @Id
    @Column(name = "desired_recruit_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id", nullable = false)
    private Recruit recruit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
