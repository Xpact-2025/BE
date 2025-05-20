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

    @Embedded // status, formType, ExperienceType 포함
    private MetaData metaData;

    @Column(name = "title", nullable = false)
    private String title;

    @Embedded
    private StarForm starForm;

    @Embedded
    private SimpleForm simpleForm;

    @Embedded
    private Qualification qualification;


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

    @Column(name = "is_ended")
    private Boolean isEnded;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

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

    // 해당 experience의 유형이 파일을 필요로 하지 않는 유형인지 확인
    public static boolean isNeedFiles(String experienceType) {
        return !ExperienceType.NOT_NEED_FILE.contains(ExperienceType.valueOf(experienceType));
    }

    // 해당 experience의 유형이 warrant를 필요로 하는 유형인지 확인
    public static boolean isNeedWarrant(String experienceType) {
        return ExperienceType.NEED_WARRNT.contains(ExperienceType.valueOf(experienceType));
    }

    public static boolean isQualification(String experienceType) {
        return ExperienceType.IS_QUALIFICATION.contains(ExperienceType.valueOf(experienceType));
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
