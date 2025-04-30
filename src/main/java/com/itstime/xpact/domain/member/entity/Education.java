package com.itstime.xpact.domain.member.entity;

import com.itstime.xpact.domain.member.common.SchoolStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "education")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Education {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "major")
    private String major;

    @Column(name = "school_status")
    @Enumerated(EnumType.STRING)
    private SchoolStatus schoolStatus;

    @Column(name ="is_verified")
    private boolean isVerified;

    @Column(name = "education_name")
    private String educationName;

    public void setMember(Member member) {
        this.member = member;
    }
}
