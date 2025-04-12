package com.itstime.xpact.domain.member.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "education")
public class Education {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
