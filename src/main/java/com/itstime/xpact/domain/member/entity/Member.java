package com.itstime.xpact.domain.member.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.member.common.ActiveStatus;
import com.itstime.xpact.domain.member.common.Role;
import com.itstime.xpact.domain.member.common.SchoolStatus;
import com.itstime.xpact.domain.member.common.Type;
import com.itstime.xpact.domain.member.dto.response.MemberInfoResponseDto;
import com.itstime.xpact.domain.recruit.entity.Recruit;
import com.itstime.xpact.domain.scrap.entity.Scrap;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "age", nullable = true)
    private int age;

    @Column(name = "imgurl")
    private String imgurl;

    @Column(name = "inactive_date")
    private LocalDate inactiveDate;

    @Column(name = "member_status")
    @Enumerated(EnumType.STRING)
    private ActiveStatus activeStatus;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "education")
    private String education;

    @Column(name = "school_status")
    @Enumerated(EnumType.STRING)
    private SchoolStatus schoolStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Scrap> scraps = new ArrayList<>();

    @Builder
    public Member(String name, String email, String password, LocalDate birthDate, Type type, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.type = type;
        this.role = role;
    }

    // 프로필 설정에 사용될 메소드
    public MemberInfoResponseDto toMemberInfoResponseDto(Member member) {
        return MemberInfoResponseDto.builder()
                .name(member.getName())
                .imgurl(member.getImgurl())
                .school(member.getEducation())
                .schoolStatus(member.getSchoolStatus().name())
                .recruit(member.getRecruit().getName())
                .build();
    }
}
