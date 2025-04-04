package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.dto.request.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.request.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "experience")
@SuperBuilder
@NoArgsConstructor
public class Experience extends BaseEntity {

    @Id
    @Column(name = "experience_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    protected Status status;

    @Column(name = "form_type", nullable = false)
    @Enumerated(EnumType.STRING)
    protected FormType formType;

    @Column(name = "title", nullable = false)
    protected String title;

    @Column(name = "is_ended", nullable = false)
    protected Boolean isEnded;

    @Column(name = "start_date")
    protected LocalDate startDate;

    @Column(name = "end_date")
    protected LocalDate endDate;

    @Column(name = "summary")
    protected String summary;

    @Column(name = "keyword")
    protected String keyword;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    protected ExperienceType experienceType;


    // STAR_FORM
    @Column(name = "situation")
    private String situation;

    @Column(name = "task")
    private String task;

    @Column(name = "action")
    private String action;

    @Column(name = "result")
    private String result;


    // SIMPLE_FORM
    @Column(name = "role")
    private String role;

    @Column(name = "perform")
    private String perform;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_recruit_id")
    private DetailRecruit detailRecruit;

    public static Experience StarForm(ExperienceCreateRequestDto createRequestDto) {
        return Experience.builder()
                .status(createRequestDto.getStatus())
                .formType(createRequestDto.getFormType())
                .title(createRequestDto.getTitle())
                .isEnded(createRequestDto.getEndDate().isBefore(LocalDate.now()))
                .startDate(createRequestDto.getStartDate())
                .endDate(createRequestDto.getEndDate())
                .keyword(createRequestDto.getKeyword())
                .experienceType(createRequestDto.getExperienceType())
                .situation(createRequestDto.getSituation())
                .task(createRequestDto.getTask())
                .action(createRequestDto.getAction())
                .result(createRequestDto.getResult())
                .role(null)
                .perform(null)
                .build();
    }

    public static Experience SimpleForm(ExperienceCreateRequestDto createRequestDto) {
        return Experience.builder()
                .status(createRequestDto.getStatus())
                .formType(createRequestDto.getFormType())
                .title(createRequestDto.getTitle())
                .isEnded(createRequestDto.getEndDate().isBefore(LocalDate.now()))
                .startDate(createRequestDto.getStartDate())
                .endDate(createRequestDto.getEndDate())
                .keyword(createRequestDto.getKeyword())
                .experienceType(createRequestDto.getExperienceType())
                .situation(null)
                .task(null)
                .action(null)
                .result(null)
                .role(createRequestDto.getRole())
                .perform(createRequestDto.getPerform())
                .build();
    }

    public void addMember(Member member) {
        this.member = member;
        member.getExperiences().add(this);
    }

    public void updateToSimpleForm(ExperienceUpdateRequestDto updateRequestDto) {
        this.status = updateRequestDto.getStatus();
        this.formType = updateRequestDto.getFormType();
        this.title = updateRequestDto.getTitle();
        this.isEnded = updateRequestDto.getEndDate().isBefore(LocalDate.now());
        this.startDate = updateRequestDto.getStartDate();
        this.endDate = updateRequestDto.getEndDate();
        this.keyword = updateRequestDto.getKeyword();
        this.experienceType = updateRequestDto.getExperienceType();
        this.situation = null;
        this.task = null;
        this.action = null;
        this.result = null;
        this.role = updateRequestDto.getRole();
        this.perform = updateRequestDto.getPerform();
    }

    public void updateToStarForm(ExperienceUpdateRequestDto updateRequestDto) {
        this.status = updateRequestDto.getStatus();
        this.formType = updateRequestDto.getFormType();
        this.title = updateRequestDto.getTitle();
        this.isEnded = updateRequestDto.getEndDate().isBefore(LocalDate.now());
        this.startDate = updateRequestDto.getStartDate();
        this.endDate = updateRequestDto.getEndDate();
        this.keyword = updateRequestDto.getKeyword();
        this.experienceType = updateRequestDto.getExperienceType();
        this.situation = updateRequestDto.getSituation();
        this.task = updateRequestDto.getTask();
        this.action = updateRequestDto.getAction();
        this.result = updateRequestDto.getResult();
        this.role = null;
        this.perform = null;
    }
}
