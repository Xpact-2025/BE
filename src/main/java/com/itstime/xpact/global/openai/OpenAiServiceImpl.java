package com.itstime.xpact.global.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.domain.dashboard.dto.response.FeedbackResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.MapResponseDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;;
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
    private final ExperienceRepository experienceRepository;

    @Async
    public void summarizeExperience(Experience experience) {
        String message = String.format("""
                역할, 내가 한 일, 성과(결과)가 드러나도록 2줄 분량으로 요약해줘\s
                요약만 출력되도록 해줘\s
                data : %s""", experience.toString());

        Prompt prompt = new Prompt(message);
        ChatResponse response = openAiChatModel.call(prompt);
        String summary = response.getResult().getOutput().getText();
        log.info("summary : {}", summary);

        experience.setSummary(summary);
        experienceRepository.save(experience);
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

    @Async
    public CompletableFuture<MapResponseDto> evaluateScore(String experiences, List<String> coreSkills) throws CustomException {

        OpenAiRequestBuilder builder = new OpenAiRequestBuilder();

        PromptTemplate template = new PromptTemplate(builder.buildScorePrompt(experiences, coreSkills));
        builder.buildScoreVariables(experiences, coreSkills).forEach(
                template::add
        );
        String message = template.render();

        Message userMessage = new UserMessage(message);
        Message systemMessage = new SystemMessage(buildSystemInstruction(coreSkills));

        // JSON 파싱
        String rawResponse = openAiChatModel.call(systemMessage, userMessage);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            log.info("응답 내용 :" + rawResponse);
            MapResponseDto result = objectMapper.readValue(rawResponse, MapResponseDto.class);
            return CompletableFuture.completedFuture(result);
        } catch (JsonProcessingException e) {
            log.info("응답 내용 :" + rawResponse);
            log.error("readValue 불가...", e);
            throw CustomException.of(ErrorCode.FAILED_OPENAI_PARSING);
        }
    }

    private String buildSystemInstruction(List<String> coreSkills) {
        StringBuilder builder = new StringBuilder();
        builder.append("You are an AI evaluator for job competency. ");
        builder.append("Explain Korean. Follow the format below.\n{\n");
        builder.append("\"coreSkillMaps\": [\n{");
        for (String coreSkill : coreSkills) {
            builder.append("\"coreSkillName\": ").append(coreSkill).append(", ");
            builder.append("\"score\": float between 0.0~10.0 },\n");
        }
        builder.append("]\n");
        builder.append("}");
        return builder.toString();
    }

    // 강점 피드백
    public CompletableFuture<FeedbackResponseDto> feedbackStrength(String experiences, String strength) {
        OpenAiRequestBuilder builder = new OpenAiRequestBuilder();

        PromptTemplate template = new PromptTemplate(builder.buildStrengthPrompt(experiences, strength));
        String message = template.render();

        Message userMessage = new UserMessage(message);
        Message systemMessage = new SystemMessage("두 개의 문단으로 답해라. 존댓말을 써라.");

        String rawResponse = openAiChatModel.call(systemMessage, userMessage);

        String[] paragraphs = rawResponse.split("\\n\\n", 2);

        FeedbackResponseDto dto = new FeedbackResponseDto();
        dto.setCoreSkillName(strength);
        dto.setExpAnalysis(paragraphs.length > 0 ? paragraphs[0].trim() : "");
        dto.setRecommend(paragraphs.length > 1 ? paragraphs[1].trim() : "");

        return CompletableFuture.completedFuture(dto);
    };

    // 약점 피드백
    public CompletableFuture<FeedbackResponseDto> feedbackWeakness(String experiences, String weakness) {
        OpenAiRequestBuilder builder = new OpenAiRequestBuilder();

        PromptTemplate template = new PromptTemplate(builder.buildWeaknessPrompt(experiences, weakness));
        String message = template.render();

        Message userMessage = new UserMessage(message);
        Message systemMessage = new SystemMessage("두 개의 문단으로 답해라. 존댓말을 써라.");

        String rawResponse = openAiChatModel.call(systemMessage, userMessage);

        String[] paragraphs = rawResponse.split("\\n\\n", 2);

        FeedbackResponseDto dto = new FeedbackResponseDto();
        dto.setCoreSkillName(weakness);
        dto.setExpAnalysis(paragraphs.length > 0 ? paragraphs[0].trim() : "");
        dto.setRecommend(paragraphs.length > 1 ? paragraphs[1].trim() : "");

        return CompletableFuture.completedFuture(dto);
    };


    public void getDetailRecruitFromExperience(Experience experience) {
        String experienceStr = experience.toString();
        String recruits = detailRecruitToString();
        String message = String.format(
                "다음 객체를 분석해서 주어진 recruit 중 가장 적절한 하나를 선택해 주세요.\n" +
                        "객체: %s\n" +
                        "recruit (각 recruit는 '/'로 분리되어 있음) : %s\n" +
                        "출력 형식 : {recruit}\n" +
                        "출력 시 다른 문구 넣지 말고 그저 선택한 recruit만 출력해야함",
                experienceStr, recruits
        );

        Prompt prompt = new Prompt(message);
        ChatResponse response = openAiChatModel.call(prompt);
        String result = response.getResult().getOutput().getText();
        log.info("result : {}", result);
        DetailRecruit detailRecruit = detailRecruitRepository.findByName(result).orElseThrow(() ->
                CustomException.of(ErrorCode.DETAIL_RECRUIT_NOT_FOUND));

        experience.setDetailRecruit(detailRecruit);
        experienceRepository.save(experience);
    }

    private String detailRecruitToString() {
        StringBuilder recruits = new StringBuilder();
        detailRecruitRepository.findAll()
                .forEach(recruit -> recruits.append(", ").append(recruit.getName()));
        return recruits.toString();
    }
}
