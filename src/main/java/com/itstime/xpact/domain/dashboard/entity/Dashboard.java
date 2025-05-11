package com.itstime.xpact.domain.dashboard.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dashboard")
public class Dashboard {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dashboard_id")
    private Long id;

    private String coreSkillMap;
}
