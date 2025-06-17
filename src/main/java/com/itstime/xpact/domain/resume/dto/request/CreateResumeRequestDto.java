package com.itstime.xpact.domain.resume.dto.request;

import com.itstime.xpact.domain.resume.entity.Resume;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateResumeRequestDto {

    private String title;
    private String question;
    private Integer limit;
    private List<RecommendExperienceDto> recommendExperiences;
    private List<Long> experienceIds;
    private List<String> keywords;
    private String structure;
    private String content;

    @Getter
    public static class RecommendExperienceDto {
        private Long id;
        private String title;
        private String linkPoint;
    }

    public static List<Resume.RecommendExperience> of(List<RecommendExperienceDto> recommendExperienceDtos) {
        return recommendExperienceDtos.stream()
                .map(recommendExperienceDto ->
                        Resume.RecommendExperience.builder()
                            .id(recommendExperienceDto.getId())
                            .title(recommendExperienceDto.getTitle())
                            .linkPoint(recommendExperienceDto.getLinkPoint())
                            .build())
                .toList();
    }
}
