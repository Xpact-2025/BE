package com.itstime.xpact.global.openai;

import com.itstime.xpact.domain.experience.entity.Experience;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface OpenAiService {

    @Async("taskExecutor")
    CompletableFuture<Void> summarizeExperience(Experience experience);

    Map<String, Map<String, String>> getCoreSkill(List<String> recruitNames);

    @Async
    CompletableFuture<String> evaluateScore(String experiences, List<String> coreSkills);

    @Async("taskExecutor")
    CompletableFuture<Void> getDetailRecruitFromExperience(Experience experience);
}
