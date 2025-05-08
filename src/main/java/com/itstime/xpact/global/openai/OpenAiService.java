package com.itstime.xpact.global.openai;

import com.itstime.xpact.domain.experience.entity.Experience;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;

public interface OpenAiService {

    @Async("taskExecutor")
    void summarizeExperience(Experience experience);

    Map<String, String> getCoreSkill(List<String> recruits);

    Map<String, String> getAllCoreSkillsInBatch(List<String> detailRecruits);
}
