package com.itstime.xpact.domain.resume.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class AiResumeRequestDto {

    private String title;
    private String question;
    private Integer limit;
    private List<Long> experienceIds;
    private List<String> keywords;

    @Override
    public String toString() {
        return "title='" + title + '\'' +
                "question='" + question + '\'' +
                "limit=" + limit +
                "keywords=" + keywords;
    }
}
