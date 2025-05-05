package com.itstime.xpact.domain.member.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.member.common.ActiveStatus;
import com.itstime.xpact.domain.member.common.Role;
import com.itstime.xpact.domain.member.common.Type;
import com.itstime.xpact.domain.member.dto.request.MemberInfoRequestDto;
import com.itstime.xpact.domain.member.dto.response.MemberInfoResponseDto;
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
    private Integer age;

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

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Education education;

    @Column(name = "desired_recruit")
    private String desiredRecruit;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Scrap> scraps = new ArrayList<>();

    @Builder
    public Member(String name, String email, String password, LocalDate birthDate, Integer age, Type type, Role role) {
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
                .education(member.getEducation().getEducationName())
                .age(member.getAge() != null ? member.getAge() : 0)
                .desiredDetailRecruit(member.getDesiredRecruit() != null ? member.getDesiredRecruit() : null)
                .startedAt(member.getEducation().getStartedAt() != null ? member.getEducation().getStartedAt() : null)
                .endedAt(member.getEducation().getEndedAt() != null ? member.getEducation().getEndedAt() : null)
                .build();
    }

    public void updateMemberInfo(MemberInfoRequestDto requestDto) {
        if (requestDto.name() != null) this.name = requestDto.name();
        if (requestDto.age() != null) this.age = requestDto.age();
        if (requestDto.imgurl() != null) this.imgurl = requestDto.imgurl();
    }

    // 최종학력만 저장
    public void setEducation(Education education) {
        this.education = education;
        education.setMember(this);
    }

    // 희망직무 저장
    public void setDesiredRecruit(String desiredRecruit) {
        this.desiredRecruit = desiredRecruit;
    }
}
