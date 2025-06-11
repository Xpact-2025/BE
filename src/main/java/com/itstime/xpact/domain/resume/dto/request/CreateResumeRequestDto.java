package com.itstime.xpact.domain.resume.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateResumeRequestDto {

    private String title;
    private String question;
    private Integer limit;
    private List<Long> experiecneIds;
    private List<String> keywords;
    private String structure;
    private String content;
}
