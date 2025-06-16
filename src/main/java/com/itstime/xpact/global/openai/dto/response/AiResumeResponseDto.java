package com.itstime.xpact.global.openai.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AiResumeResponseDto {
    private String structure;
    private String content;
}
