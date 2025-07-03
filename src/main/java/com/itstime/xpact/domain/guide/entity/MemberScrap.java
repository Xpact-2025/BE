package com.itstime.xpact.domain.guide.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "member_scrap", uniqueConstraints = {@UniqueConstraint(columnNames = {"member_id", "scrap_id"})})
@NoArgsConstructor
@AllArgsConstructor
public class MemberScrap extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_scrap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrap_id")
    private Scrap scrap;
}
