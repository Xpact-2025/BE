package com.itstime.xpact.domain.experience.service;

import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.entity.SubExperience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.global.openai.OpenAiService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExperienceScheduler {

    private final ExperienceRepository experienceRepository;
    private final OpenAiService openAiService;

    @Scheduled(cron = "0 0 4 * * *")
    public void checkSummarizedExperience() {
        List<Experience> experiences = experienceRepository.findAllWithSubExperiences();
        log.info("Check all of Experiences...");
        for (Experience experience : experiences) {
            if (experience.getSummary() == null) {
                log.info("Insert summarization to Experience : {}...", experience.getId());
                List<SubExperience> subExperiences = experience.getSubExperiences();
                openAiService.summarizeExperience(experience, subExperiences);
            }
        }
    }
}
