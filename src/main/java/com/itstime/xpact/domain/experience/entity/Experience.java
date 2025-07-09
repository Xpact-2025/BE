package com.itstime.xpact.domain.experience.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.dashboard.dto.response.TimelineResponseDto;
import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.dto.request.ExperienceUpdateRequestDto;
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

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "publisher")
    private String publisher;

    @Setter
    @Column(name = "summary", length = 512)
    private String summary;

    @Builder.Default
    @OneToMany(mappedBy = "experience", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SubExperience> subExperiences = new ArrayList<>();

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_recruit_id")
    private DetailRecruit detailRecruit;



    public static TimelineResponseDto toTimeLineDto(Experience experience) {
            return TimelineResponseDto.builder()
                    .startDate(experience.getStartDate())
                    .endDate(experience.getEndDate())
                    .title(experience.getTitle())
                    .experienceType(experience.getExperienceType())
                    .build();
    }

    public void updateExperience(ExperienceUpdateRequestDto updateRequestDto) {
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

    @Override
    public String toString() {
        return "Experience{" +
                ", experienceType=" + experienceType +
                ", title='" + title + '\'' +
                ", qualification='" + qualification + '\'' +
                ", publisher='" + publisher + '\'' +
                '}';
    }

}
