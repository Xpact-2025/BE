package com.itstime.xpact.domain.member.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.guide.entity.MemberScrap;
import com.itstime.xpact.domain.member.common.ActiveStatus;
import com.itstime.xpact.domain.member.common.Role;
import com.itstime.xpact.domain.member.common.Type;
import com.itstime.xpact.domain.member.dto.request.MemberSaveRequestDto;
import com.itstime.xpact.domain.member.dto.response.EducationSaveResponseDto;
import com.itstime.xpact.domain.member.dto.response.MemberSaveResponseDto;
import com.itstime.xpact.domain.member.dto.response.MypageInfoResponseDto;
import com.itstime.xpact.domain.recruit.dto.response.DesiredRecruitResponseDto;
import com.itstime.xpact.domain.guide.entity.Weakness;
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

    @Setter
    @Column(name = "image_url")
    private String imageUrl;

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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Weakness> weaknessList;

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberScrap> memberScrapList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new ArrayList<>();

    @Builder
    public Member(String name, String email, String password, LocalDate birthDate, Integer age, Type type, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.age = age;
        this.type = type;
        this.role = role;
    }

    // 프로필 설정에 사용될 메소드
    public MypageInfoResponseDto toMypageInfoResponseDto(Member member) {
        return MypageInfoResponseDto.builder()
                .name(member.getName())
                .imgurl(member.getImageUrl() != null ? member.getImageUrl() : null)
                .age(member.getAge() != null ? member.getAge() : 0)
                .educationDegree(member.getEducation() != null && member.getEducation().getDegree() != null
                        ? member.getEducation().getDegree().getDisplayName() : null)
                .educationName(
                        member.getEducation() != null && member.getEducation().getEducationName() != null
                                ? member.getEducation().getEducationName()
                                : null
                )
                .schoolState(
                        member.getEducation() != null && member.getEducation().getSchoolStatus() != null
                                ? member.getEducation().getSchoolStatus().getDisplayName()
                        : null
                )
                .desiredDetailRecruit(member.getDesiredRecruit() != null ? member.getDesiredRecruit() : null)
                .build();
    }

    public MemberSaveResponseDto toMemberSaveResponseDto(Member member, EducationSaveResponseDto educationDto, DesiredRecruitResponseDto desiredRecruitDto) {
        return MemberSaveResponseDto.builder()
                .name(member.getName())
                .imgurl(member.getImageUrl() != null ? member.getImageUrl() : null)
                .age(member.getAge() != null ? member.getAge() : 0)
                .educationDegree(
                        member.getEducation() != null && member.getEducation().getEducationName() != null
                        ? member.getEducation().getDegree().getDisplayName() : null
                )
                .educationName(
                        member.getEducation() != null && member.getEducation().getEducationName() != null
                                ? member.getEducation().getEducationName()
                                : null
                )
                .desiredDetailRecruit(member.getDesiredRecruit() != null ? member.getDesiredRecruit() : null)
                .educationSaveResponseDto(educationDto != null ? educationDto : null)
                .desiredRecruitResponseDto(desiredRecruitDto != null ? desiredRecruitDto : null)
                .build();
    }


    public void updateMemberInfo(MemberSaveRequestDto requestDto) {
        if (requestDto.name() != null) this.name = requestDto.name();
        if (requestDto.age() != null) this.age = requestDto.age();
        if (requestDto.imgUrl() != null) this.imageUrl = requestDto.imgUrl();
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
