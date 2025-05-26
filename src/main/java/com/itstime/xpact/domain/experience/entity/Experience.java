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

//    @Embedded
//    private Qualification qualification;


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
    @JoinColumn(name = "member_id")
    private Member member;

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

    public static Experience starForm(ExperienceCreateRequestDto createRequestDto) {
        return Experience.builder()
                .metaData(MetaData.builder()
                        .status(Status.valueOf(createRequestDto.getStatus()))
                        .formType(FormType.valueOf(createRequestDto.getFormType()))
                        .experienceType(ExperienceType.valueOf(createRequestDto.getExperienceType())).build())
                .isEnded(createRequestDto.getEndDate().isBefore(LocalDate.now()))
                .startDate(createRequestDto.getStartDate())
                .endDate(createRequestDto.getEndDate())
                .title(createRequestDto.getTitle())
                .simpleForm(SimpleForm.builder().build())
                .starForm(StarForm.builder()
                        .situation(createRequestDto.getSituation())
                        .task(createRequestDto.getTask())
                        .action(createRequestDto.getAction())
                        .result(createRequestDto.getResult()).build())
                .qualification(null)
                .build();
    }


    public static Experience simpleForm(ExperienceCreateRequestDto createRequestDto) {
        return Experience.builder()
                .metaData(MetaData.builder()
                        .status(Status.valueOf(createRequestDto.getStatus()))
                        .formType(FormType.valueOf(createRequestDto.getFormType()))
                        .experienceType(ExperienceType.valueOf(createRequestDto.getExperienceType())).build())
                .isEnded(createRequestDto.getEndDate().isBefore(LocalDate.now()))
                .startDate(createRequestDto.getStartDate())
                .endDate(createRequestDto.getEndDate())
                .title(createRequestDto.getTitle())
                .simpleForm(SimpleForm.builder()
                        .role(createRequestDto.getRole())
                        .perform(createRequestDto.getPerform()).build())
                .starForm(StarForm.builder().build())
                .qualification(Qualification.builder().build())
                .build();
    }

    public static Experience qualification(ExperienceCreateRequestDto createRequestDto) {
        return Experience.builder()
                .metaData(MetaData.builder()
                        .status(Status.valueOf(createRequestDto.getStatus()))
                        .formType(FormType.valueOf(createRequestDto.getFormType()))
                        .experienceType(ExperienceType.valueOf(createRequestDto.getExperienceType())).build())
                .isEnded(createRequestDto.getIssueDate().isBefore(LocalDate.now()))
                .startDate(createRequestDto.getStartDate())
                .endDate(createRequestDto.getEndDate())
                .title(null)
                .simpleForm(SimpleForm.builder().build())
                .starForm(StarForm.builder().build())
                .qualification(Qualification.builder()
                        .qualification(createRequestDto.getQualification())
                        .publisher(createRequestDto.getPublisher())
                        .simpleDescription(createRequestDto.getSimpleDescription()).build())
                .keywords(null)
                .build();
    }

    public void updateToSimpleForm(ExperienceUpdateRequestDto updateRequestDto) {
        this.metaData = MetaData.builder()
                .status(Status.valueOf(updateRequestDto.getStatus()))
                .formType(FormType.valueOf(updateRequestDto.getFormType()))
                .experienceType(ExperienceType.valueOf(updateRequestDto.getExperienceType())).build();
        this.isEnded = updateRequestDto.getEndDate().isBefore(LocalDate.now());
        this.startDate = updateRequestDto.getStartDate();
        this.endDate = updateRequestDto.getEndDate();
        this.title = updateRequestDto.getTitle();
        this.simpleForm = SimpleForm.builder()
                .role(updateRequestDto.getRole())
                .perform(updateRequestDto.getPerform()).build();
        this.starForm = StarForm.builder().build();
        this.qualification = Qualification.builder().build();
    }

    public void updateToStarForm(ExperienceUpdateRequestDto updateRequestDto) {
        this.metaData = MetaData.builder()
                .status(Status.valueOf(updateRequestDto.getStatus()))
                .formType(FormType.valueOf(updateRequestDto.getFormType()))
                .experienceType(ExperienceType.valueOf(updateRequestDto.getExperienceType())).build();
        this.isEnded = updateRequestDto.getEndDate().isBefore(LocalDate.now());
        this.startDate = updateRequestDto.getStartDate();
        this.endDate = updateRequestDto.getEndDate();
        this.title = updateRequestDto.getTitle();
        this.simpleForm = SimpleForm.builder().build();
        this.starForm = StarForm.builder()
                .situation(updateRequestDto.getSituation())
                .task(updateRequestDto.getTask())
                .action(updateRequestDto.getAction())
                .result(updateRequestDto.getResult()).build();
        this.qualification = Qualification.builder().build();
    }

    public void updateToQualification(ExperienceUpdateRequestDto updateRequestDto) {
        this.metaData = MetaData.builder()
                .status(Status.valueOf(updateRequestDto.getStatus()))
                .formType(FormType.valueOf(updateRequestDto.getFormType()))
                .experienceType(ExperienceType.valueOf(updateRequestDto.getExperienceType())).build();
        this.isEnded = updateRequestDto.getEndDate().isBefore(LocalDate.now());
        this.startDate = updateRequestDto.getStartDate();
        this.endDate = updateRequestDto.getEndDate();
        this.title = null;
        this.starForm = StarForm.builder().build();
        this.simpleForm = SimpleForm.builder().build();
        this.qualification = Qualification.builder()
                .qualification(updateRequestDto.getQualification())
                .publisher(updateRequestDto.getPublisher())
                .simpleDescription(updateRequestDto.getSimpleDescription()).build();
    }


    public static TimelineResponseDto toTimeLineDto(Experience experience) {
            return TimelineResponseDto.builder()
                    .startDate(experience.getStartDate())
                    .endDate(experience.getEndDate())
                    .title(experience.getTitle())
                    .experienceType(experience.getMetaData().getExperienceType())
                    .build();
    }
}
