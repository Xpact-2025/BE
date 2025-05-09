package com.itstime.xpact.domain.member.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "school",
    uniqueConstraints = @UniqueConstraint(columnNames =  {"school_name", "major"}))
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class School extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_id", nullable = false)
    private Long id;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "major")
    private String major;
}
