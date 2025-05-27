package com.itstime.xpact.global.webclient.openai;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OpenAiRequestDto {
    private String experiences;
    private List<String> coreSkills;
}
