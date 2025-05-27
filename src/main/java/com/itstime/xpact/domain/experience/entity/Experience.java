package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.dashboard.dto.response.TimelineResponseDto;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.FormType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.dto.request.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.request.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.entity.embeddable.*;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.itstime.xpact.domain.experience.common.ExperienceType.IS_QUALIFICATION;

@Getter
@Entity
@Table(name = "experience")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"detailRecruit"})
public class Experience extends BaseEntity {

    @Id
    @Column(name = "experience_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExperienceType experienceType;

    @Column(name = "title")
    private String title;

    @Column(name = "is_ended")
    private Boolean isEnded;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;


    @Column(name = "sub_title")
    private String subTitle;

    @Column(name = "form_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FormType formType;

    @Embedded
    private StarForm starForm;

    @Embedded
    private SimpleForm simpleForm;

    @Column(name = "qualification")
    private String qualification;
    @Column(name = "publisher")
    private String publisher;
    @Column(name = "simple_description", length = 512)
    private String simpleDescription;

    @Setter
    @Column(name = "summary", length = 512)
    private String summary;

    @Builder.Default
    @OneToMany(mappedBy = "experience", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Keyword> keywords = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "experience", cascade =  CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_experience_id")
    private GroupExperience groupExperience;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_recruit_id")
    private DetailRecruit detailRecruit;

    public void setKeywords(List<Keyword> keywords) {
        this.keywords.clear();
        this.keywords.addAll(keywords);
    }

    public void setFiles(List<File> files) {
        this.files.clear();
        this.files.addAll(files);
    }


    public static TimelineResponseDto toTimeLineDto(Experience experience) {
            return TimelineResponseDto.builder()
                    .startDate(experience.getStartDate())
                    .endDate(experience.getEndDate())
                    .title(experience.getTitle())
                    .experienceType(experience.getExperienceType())
                    .build();
    }

    public void updateCommonFields(ExperienceUpdateRequestDto updateRequestDto) {
        if(IS_QUALIFICATION.contains(ExperienceType.valueOf(updateRequestDto.getExperienceType()))) {
            this.experienceType = ExperienceType.valueOf(updateRequestDto.getExperienceType());
            this.qualification = updateRequestDto.getQualification();
            this.publisher = updateRequestDto.getPublisher();
            this.startDate = updateRequestDto.getStartDate();
            this.endDate = updateRequestDto.getEndDate();
            this.isEnded = updateRequestDto.getEndDate().isBefore(LocalDate.now());
        } else {
            this.experienceType = ExperienceType.valueOf(updateRequestDto.getExperienceType());
            this.title = updateRequestDto.getTitle();
            this.startDate = updateRequestDto.getStartDate();
            this.endDate = updateRequestDto.getEndDate();
            this.isEnded = updateRequestDto.getEndDate().isBefore(LocalDate.now());
        }
    }

    public void updateSubFields(ExperienceUpdateRequestDto.SubExperience targetExperience, ExperienceType experienceType) {
        if(IS_QUALIFICATION.contains(experienceType)) {
            this.subTitle = targetExperience.getSubTitle();
            this.formType = FormType.STAR_FORM;
            this.simpleDescription = targetExperience.getSimpleDescription();
            this.status = Status.valueOf(targetExperience.getStatus());
        } else {
            switch (FormType.valueOf(targetExperience.getFormType())) {
                case STAR_FORM -> {
                    this.subTitle = targetExperience.getSubTitle();
                    this.status = Status.valueOf(targetExperience.getStatus());
                    this.formType = FormType.STAR_FORM;
                    this.starForm = StarForm.builder()
                            .situation(targetExperience.getSituation())
                            .task(targetExperience.getTask())
                            .action(targetExperience.getAction())
                            .result(targetExperience.getResult())
                            .build();
                }
                case SIMPLE_FORM -> {
                    this.subTitle = targetExperience.getSubTitle();
                    this.status = Status.valueOf(targetExperience.getStatus());
                    this.formType = FormType.SIMPLE_FORM;
                    this.simpleForm = SimpleForm.builder()
                            .perform(targetExperience.getPerform())
                            .role(targetExperience.getRole())
                            .build();
                }
            }
        }
    }
}
