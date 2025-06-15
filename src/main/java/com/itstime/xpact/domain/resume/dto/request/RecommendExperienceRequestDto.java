package com.itstime.xpact.domain.resume.dto.request;

import lombok.Getter;

@Getter
public class RecommendExperienceRequestDto {
    private String title;
    private String question;

    @Override
    public String toString() {
        return "title='" + title + '\'' +
                "question='" + question;
    }
}
