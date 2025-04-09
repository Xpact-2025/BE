package com.itstime.xpact.global.openai;

import com.itstime.xpact.domain.experience.entity.Experience;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    private final OpenAiChatModel openAiChatModel;

    @Async
    public CompletableFuture<String> summarizeExperience(Experience experience) {
        String message = String.format("역할, 내가 한 일, 성과(결과)가 드러나게 자세한 `~~했음`으로 끝나게 2줄로 요약해줘 : %s", experience.toString());
        log.info(message);

        Prompt prompt = new Prompt(message);
        ChatResponse response = openAiChatModel.call(prompt);
        String result = response.getResult().getOutput().getText();
        log.info("result : {}", result);

        return CompletableFuture.completedFuture(result);
    }
}
