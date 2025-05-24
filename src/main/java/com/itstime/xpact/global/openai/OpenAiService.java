package com.itstime.xpact.global.openai;

import com.itstime.xpact.domain.dashboard.dto.response.FeedbackResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.MapResponseDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface OpenAiService {

    @Async("taskExecutor")
    void summarizeExperience(Experience experience);

    Map<String, Map<String, String>> getCoreSkill(List<String> recruitNames);

    @Async("taskExecutor")
    CompletableFuture<MapResponseDto> evaluateScore(String experiences, List<String> coreSkills);

    @Async("taskExecutor")
    void getDetailRecruitFromExperience(Experience experience);

    CompletableFuture<FeedbackResponseDto> feedbackStrength(String experiences, String strength);
    CompletableFuture<FeedbackResponseDto> feedbackWeakness(String experiences, String weakness);
}
