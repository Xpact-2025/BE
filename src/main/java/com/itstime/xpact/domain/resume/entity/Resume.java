package com.itstime.xpact.domain.resume.entity;

import com.itstime.xpact.domain.common.BaseEntity;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.resume.converter.RecommendExperienceListConverter;
import com.itstime.xpact.domain.resume.dto.request.UpdateResumeRequestDto;
import com.itstime.xpact.domain.resume.entity.embeddable.RecommendExperience;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "resume")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume extends BaseEntity {

    @Id
    @Column(name = "resume_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "question")
    private String question;

    @Column(name = "text_limit")
    private Integer limit;

    @Column(name = "keywords")
    private List<String> keywords;

    @Column(name = "recommend_experiences")
    @Convert(converter = RecommendExperienceListConverter.class)
    private List<RecommendExperience>  recommendExperiences;

    @Column(name = "structure")
    private String structure;

    @Column(name = "content", length = 1024)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @JoinTable(
            name = "resume_experience",
            joinColumns = @JoinColumn(name = "resume_id"),
            inverseJoinColumns = @JoinColumn(name = "experience_id")
    )
    @OneToMany(fetch = FetchType.LAZY)
    private List<Experience> experiences = new ArrayList<>();

    // Experience에서 resume으로 조회하는 로직이 없으므로 Resume -> Experience의 단방향 관계로 설정

    public void updateResume(UpdateResumeRequestDto updateResumeRequestDto) {
        this.title = updateResumeRequestDto.getTitle();
        this.question = updateResumeRequestDto.getQuestion();
        this.limit = updateResumeRequestDto.getLimit();
        this.structure = updateResumeRequestDto.getStructure();
        this.content = updateResumeRequestDto.getContent();
    }
}