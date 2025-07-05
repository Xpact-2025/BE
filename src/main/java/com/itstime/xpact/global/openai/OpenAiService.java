package com.itstime.xpact.global.openai;

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
    void getDetailRecruitFromExperience(Experience experience);

    @Async
    CompletableFuture<String> analysisWeakness(String weakness, String experiences);

    List<String> getRecommendActivities(String weakness);
}
