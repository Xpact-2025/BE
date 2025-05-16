package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.dashboard.dto.response.TimelineResponseDto;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.dto.request.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.request.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "experience")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"member", "detailRecruit"})
public class Experience extends BaseEntity {

    @Id
    @Column(name = "experience_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "form_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FormType formType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "is_ended", nullable = false)
    private Boolean isEnded;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Setter
    @Column(name = "summary")
    private String summary;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExperienceType experienceType;

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

    @OneToMany(mappedBy = "experience", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Keyword> keywords = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_recruit_id")
    private DetailRecruit detailRecruit;

    public static Experience StarForm(ExperienceCreateRequestDto createRequestDto) {
        return Experience.builder()
                .status(Status.valueOf(createRequestDto.getStatus()))
                .formType(FormType.valueOf(createRequestDto.getFormType()))
                .title(createRequestDto.getTitle())
                .isEnded(createRequestDto.getEndDate().isBefore(LocalDate.now()))
                .startDate(createRequestDto.getStartDate())
                .endDate(createRequestDto.getEndDate())
                .experienceType(ExperienceType.valueOf(createRequestDto.getExperienceType()))
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
                .status(Status.valueOf(createRequestDto.getStatus()))
                .formType(FormType.valueOf(createRequestDto.getFormType()))
                .title(createRequestDto.getTitle())
                .isEnded(createRequestDto.getEndDate().isBefore(LocalDate.now()))
                .startDate(createRequestDto.getStartDate())
                .endDate(createRequestDto.getEndDate())
                .experienceType(ExperienceType.valueOf(createRequestDto.getExperienceType()))
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
        updateCommonFields(updateRequestDto);
        this.situation = null;
        this.task = null;
        this.action = null;
        this.result = null;
        this.role = updateRequestDto.getRole();
        this.perform = updateRequestDto.getPerform();
    }

    public void updateToStarForm(ExperienceUpdateRequestDto updateRequestDto) {
        updateCommonFields(updateRequestDto);
        this.situation = updateRequestDto.getSituation();
        this.task = updateRequestDto.getTask();
        this.action = updateRequestDto.getAction();
        this.result = updateRequestDto.getResult();
        this.role = null;
        this.perform = null;
    }

    private void updateCommonFields(ExperienceUpdateRequestDto updateRequestDto) {
        this.status = Status.valueOf(updateRequestDto.getStatus());
        this.formType = FormType.valueOf(updateRequestDto.getFormType());
        this.title = updateRequestDto.getTitle();
        this.isEnded = updateRequestDto.getEndDate().isBefore(LocalDate.now());
        this.startDate = updateRequestDto.getStartDate();
        this.endDate = updateRequestDto.getEndDate();
        this.keywords.clear();
        this.keywords.addAll(updateRequestDto.getKeywords().stream()
                .map(keywordStr -> Keyword.builder()
                        .name(keywordStr)
                        .experience(this)
                        .build()).toList());

        this.experienceType = ExperienceType.valueOf(updateRequestDto.getExperienceType());
    }

    public void setKeyword(List<Keyword> keywords) {
        this.keywords = keywords;
    }


    public static TimelineResponseDto toTimeLineDto(Experience experience) {
            return TimelineResponseDto.builder()
                    .startDate(experience.getStartDate())
                    .endDate(experience.getEndDate())
                    .title(experience.getTitle())
                    .experienceType(experience.getExperienceType())
                    .build();
    }
}
