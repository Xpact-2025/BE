package com.itstime.xpact.domain.resume.dto.response;

import com.itstime.xpact.domain.resume.entity.Resume;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DetailResumeResponseDto {

    private String title;
    private String question;
    private Integer limit;
    private List<ExperienceInfo> experiecnes;
    private List<String> keywords;
    private String structure;
    private String content;

    @Builder
    static class ExperienceInfo {
        private Long id;
        private String title;
    }

    public static DetailResumeResponseDto of(Resume resume) {
        return DetailResumeResponseDto.builder()
                .title(resume.getTitle())
                .question(resume.getQuestion())
                .limit(resume.getLimit())
                .experiecnes(resume.getExperiences().stream()
                        .map(exp -> ExperienceInfo.builder()
                                .id(exp.getId())
                                .title(exp.getTitle())
                                .build())
                        .toList())
                .keywords(resume.getKeywords())
                .structure(resume.getStructure())
                .content(resume.getContent())
                .build();

    }
}
