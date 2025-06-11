package com.itstime.xpact.domain.resume.service;

import com.itstime.xpact.domain.experience.dto.response.RecommendExperienceResponseDto;
import com.itstime.xpact.domain.resume.dto.request.AiResumeRequestDto;
import com.itstime.xpact.domain.resume.dto.request.RecommendExperienceRequestDto;
import com.itstime.xpact.global.openai.dto.response.ResumeResponseDto;
import com.itstime.xpact.global.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiResumeService {

    private final OpenAiService openAiService;

    public List<RecommendExperienceResponseDto> getRecommendExperience(RecommendExperienceRequestDto requestDto) {
        return openAiService.getRecommendExperience(requestDto);
    }

    public ResumeResponseDto createResume(AiResumeRequestDto responseDto) {
        return openAiService.createResume(responseDto);
    }
}
