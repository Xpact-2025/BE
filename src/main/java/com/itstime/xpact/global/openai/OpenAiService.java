package com.itstime.xpact.global.openai;

import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.entity.SubExperience;
import com.itstime.xpact.domain.guide.entity.Weakness;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface OpenAiService {

    @Async("taskExecutor")
    void summarizeExperience(Experience experience, List<SubExperience> subExperiences);

    Map<String, Map<String, String>> getCoreSkill(List<String> recruitNames);

    @Async("taskExecutor")
    void getDetailRecruitFromExperience(Experience experience);

    @Async
    CompletableFuture<String> analysisWeakness(String weakness, String experiences);

    String getRecommendActivitiesByExperiecnes(List<Weakness> weaknesses);
}
