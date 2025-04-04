package com.itstime.xpact.global.openai;

import com.itstime.xpact.domain.experience.entity.Experience;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface OpenAiService {

    @Async("taskExecutor")
    CompletableFuture<String> summarizeExperience(Experience experience);

}
