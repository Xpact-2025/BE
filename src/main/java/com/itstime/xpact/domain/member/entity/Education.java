package com.itstime.xpact.domain.member.entity;

import com.itstime.xpact.domain.member.common.Degree;
import com.itstime.xpact.domain.member.common.SchoolStatus;
import com.itstime.xpact.domain.member.dto.request.EducationSaveRequestDto;
import jakarta.persistence.*;
import lombok.*;

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


    public void setMember(Member member) {
        this.member = member;
    }

    public void updateEducation(EducationSaveRequestDto requestDto) {
        if (requestDto.degree() != null) this.degree = requestDto.degree();
        if (requestDto.name() != null) this.schoolName = requestDto.name();
        if (requestDto.major() != null) this.major = requestDto.major();
        if (requestDto.schoolStatus() != null) this.schoolStatus = requestDto.schoolStatus();
    }
}
