package com.itstime.xpact.domain.member.entity;

import com.itstime.xpact.domain.member.common.Degree;
import com.itstime.xpact.domain.member.common.SchoolStatus;
import com.itstime.xpact.domain.member.dto.request.EducationSaveRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "education")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Education {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "degree")
    @Enumerated(EnumType.STRING)
    private Degree degree;

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

    @Column(name = "education_name")
    private String educationName;

    @Column(name = "started_at")
    private LocalDate startedAt;

    @Column(name = "ended_at")
    private LocalDate endedAt;

    public void setMember(Member member) {
        this.member = member;
    }

    public void updateEducation(EducationSaveRequestDto requestDto) {
        this.educationName = requestDto.name();
        this.major = requestDto.major();
        this.schoolStatus = requestDto.schoolStatus();
        this.startedAt = requestDto.startedAt();
        this.endedAt = requestDto.endedAt() != null ? requestDto.endedAt() : null;
    }
}
