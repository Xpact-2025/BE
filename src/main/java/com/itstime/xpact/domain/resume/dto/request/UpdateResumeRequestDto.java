package com.itstime.xpact.domain.resume.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateResumeRequestDto {

    private String title;
    private String question;
    private Integer limit;
    private String structure;
    private String content;
}
