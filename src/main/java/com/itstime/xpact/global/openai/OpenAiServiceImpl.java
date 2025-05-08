package com.itstime.xpact.global.openai;

import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.service.MemberService;
import com.itstime.xpact.global.auth.SecurityProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    private final OpenAiChatModel openAiChatModel;
    private final MemberService memberService;
    private final SecurityProvider securityProvider;

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

    public Map<String, String> getCoreSkill(List<String> recruits) {
        Map<String, String> coreSkillOfRecruit = new HashMap<>();
        String joinedRecruits = String.join(",", recruits);

        String message = String.format("다음에 ','로 구분되어 주어질 일련의 직무에 대해 필수적으로 요하는 구체적인 핵심 스킬을 5가지 도출해줘 (전공자의 시선에서 이해할 수 잇도록)\n" +
                "출력 예시는 다음과 같아 (숫자 붙이지 마) ex) {직무}-{키워드1}/{키워드2}/{키워드3}/{키워드4}/{키워드5}\n\n" +
                "%s", joinedRecruits);

        Prompt prompt = new Prompt(message);
        log.info("Requesting core skill extraction from OpenAI...");
        ChatResponse response = openAiChatModel.call(prompt);
        String result = response.getResult().getOutput().getText();

        Arrays.stream(result.split("\n")).forEach(string -> {
            String[] row = string.split("-");
            coreSkillOfRecruit.put(row[0], row[1].trim());
        });

        return coreSkillOfRecruit;
    }

    // 점수를 매기는 메소드
    public Map<String, Double> scoreSummary(String summary) {

        Long memberId = securityProvider.getCurrentMemberId();
        Member member;
        return  null;
    }
}
