package com.itstime.xpact.global.openai;

import com.itstime.xpact.domain.experience.entity.Experience;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.Future;

public interface OpenAiService {

    // TODO OpenAIService의 경험요약을 처리하는 summarizeContentOfExperience 개발
    @Async("taskExecutor")
    Future<String> summarizeExperience(Experience experience);

}
