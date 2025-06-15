package com.itstime.xpact.global.openai.service;

import com.itstime.xpact.domain.experience.dto.response.RecommendExperienceResponseDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.resume.dto.request.AiResumeRequestDto;
import com.itstime.xpact.domain.resume.dto.request.RecommendExperienceRequestDto;
import com.itstime.xpact.global.openai.dto.response.ResumeResponseDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface OpenAiService {

    @Async("taskExecutor")
    void summarizeExperience(Experience experience);

    Map<String, Map<String, String>> getCoreSkill(List<String> recruitNames);

    @Async("taskExecutor")
    void getDetailRecruitFromExperience(Experience experience);

    String createResume(AiResumeRequestDto requestDto, List<Experience> experiences);

    String getRecommendExperience(RecommendExperienceRequestDto requestDto, List<Experience> experiences);
}
