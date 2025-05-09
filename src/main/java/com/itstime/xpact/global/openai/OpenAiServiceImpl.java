package com.itstime.xpact.global.openai;

import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.domain.recruit.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    private final OpenAiChatModel openAiChatModel;
    private final DetailRecruitRepository detailRecruitRepository;

    @Async
    public void summarizeExperience(Experience experience) {
        String message = String.format("역할, 내가 한 일, 성과(결과)가 드러나게 자세한 `~~했음`으로 끝나게 2줄로 요약해줘 : %s", experience.toString());
        log.info(message);

        Prompt prompt = new Prompt(message);
        ChatResponse response = openAiChatModel.call(prompt);
        String result = response.getResult().getOutput().getText();
        log.info("result : {}", result);

        CompletableFuture.completedFuture(result);
    }

    public Map<String, Map<String, String>> getCoreSkill(List<String> recruitNames) {
        Map<String, Map<String, String>> result = new LinkedHashMap<>();

        for (String recruitName : recruitNames) {
            log.info("Requesting core skill extraction from OpenAI for recruit: {}", recruitName);
            List<DetailRecruit> detailRecruits = detailRecruitRepository.findAllByRecruitName(recruitName);

            if (detailRecruits.isEmpty()) {
                log.warn("{} : 해당되는 DetailRecruit 조회 불가", recruitName);
                continue;
            }

            Map<String, String> coreSkillsForDetails = new LinkedHashMap<>();

            String joinedDetailNames = detailRecruits.stream()
                    .map(DetailRecruit::getName)
                    .collect(Collectors.joining(","));

            log.info("Requesting core skill extraction from OpenAI for detailRecruits: {}", joinedDetailNames);

            String promptMessage = String.format(
                    "다음 ','로 구분된 직무에 대해 반드시 요구되는 핵심 스킬 5가지를 도출해줘.(숫자 넣지 마)\n" +
                            "출력 형식: {직무}-{핵심스킬1}/{핵심2}/{핵심스킬3}/{핵심스킬4}/{핵심스킬5}\n\n" +
                            "출력 시 직무는 변형 없이 그대로 출력해라. -와 /는 필수이다.\n" +
                            "%s", joinedDetailNames);

            Prompt prompt = new Prompt(promptMessage);
            ChatResponse response = openAiChatModel.call(prompt);
            String responseText = response.getResult().getOutput().getText();

            Arrays.stream(responseText.split("\n")).forEach(string -> {
                String[] row = string.split("-");
                coreSkillsForDetails.put(row[0], row[1].trim());
            });
            result.put(recruitName, coreSkillsForDetails);
        }

        return result;
    }
}
