package com.itstime.xpact.global.webclient.openai;

import com.itstime.xpact.domain.dashboard.dto.response.SkillMapResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class LambdaOpenAiClient {

    private final WebClient lambdaWebClient;

    @Async
    public CompletableFuture<SkillMapResponseDto> requestEvaluation(OpenAiRequestDto requestDto) {
        CompletableFuture<SkillMapResponseDto> resultDto = lambdaWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/skillmaps")
                        .build())
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(SkillMapResponseDto.class)
                .toFuture();

        return resultDto;
    }
}
