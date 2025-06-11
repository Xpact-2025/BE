package com.itstime.xpact.domain.resume.dto.request;

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
    private List<Long> experienceIds;
    private List<String> keywords;
    private String structure;
    private String content;
}
