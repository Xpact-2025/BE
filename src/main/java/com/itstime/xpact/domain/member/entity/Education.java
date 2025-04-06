package com.itstime.xpact.domain.member.entity;

import com.itstime.xpact.domain.member.common.SchoolStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "education")
public class Education {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "education_id")
    private Long id;

    @Column(name = "school_name", nullable = false)
    private String schoolName;

    @Column(name = "major")
    private String major;

    @Column(name = "school_status")
    @Enumerated(EnumType.STRING)
    private SchoolStatus schoolStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
