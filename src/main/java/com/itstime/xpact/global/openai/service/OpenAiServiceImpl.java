package com.itstime.xpact.global.openai.service;

import com.itstime.xpact.domain.experience.converter.ExperienceConverter;
import com.itstime.xpact.domain.experience.dto.response.RecommendExperienceResponseDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.domain.resume.dto.request.AiResumeRequestDto;
import com.itstime.xpact.domain.resume.dto.request.RecommendExperienceRequestDto;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.openai.dto.response.ResumeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    private final OpenAiChatModel openAiChatModel;
    private final ExperienceConverter experienceConverter;
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

        Experience fresh = experienceRepository.findById(experience.getId()).orElseThrow();

        fresh.setSummary(summary);
        experienceRepository.save(fresh);
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
                GeneralException.of(ErrorCode.DETAIL_RECRUIT_NOT_FOUND));

        Experience fresh = experienceRepository.findById(experience.getId()).orElseThrow();
        fresh.setDetailRecruit(detailRecruit);
        experienceRepository.save(fresh);
    }

    /**
     *
     * @param requestDto (자기소개서 제목, 문항, 글자 수)
     * @param experiences (해당 experiences 중 추천)
     * @return RecommendExperienceResponseDto (experience id, title, linkPoint(이유)
     */
    public String getRecommendExperience(RecommendExperienceRequestDto requestDto, List<Experience> experiences) {
        String experienceStr = experiencesToString(experiences).toString();

        SystemMessage systemMessage = new SystemMessage("""
                너는 JSON 응답만 출력하는 AI야. 아래 클래스 형식에 맞춰 응답해. (```json ``` 포함 엄금)
                [
                    {
                      "id": long,
                      "title": string,
                      "linkPoint": string
                    },
                    {
                      "id": long,
                      "title": string,
                      "linkPoint": string
                    },
                    ...
                ]
                반드시 JSON만 출력해. 설명이나 불필요한 텍스트 금지.""");

        String userString = String.format("제목 : %s\n" + "문항 : %s\n" +
                "을 분석하여 기반으로 작성할 만한 경험들을 1~3개정도 아래 경험 데이터에서 선택해줘, 응답 필드의 title은 선택한 경험의 title을 그대로 쓰고, linkPoint는 왜 그 경험을 선택했는지 서술해줘"
                + "\n%s", requestDto.getTitle(), requestDto.getQuestion(), experienceStr);

        UserMessage userMessage = new UserMessage(userString);

        System.out.println("userMessage = " + userMessage);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse chatResponse = openAiChatModel.call(prompt);

        String result = chatResponse.getResult().getOutput().getText();
        log.info("result : {}", result);
        return result;
    }

    /**
     * 선택과 경험 키워드 및 문항을 중심으로 일정 글자의 자기소개서 생성
     */
    public String createResume(AiResumeRequestDto requestDto, List<Experience> experiences) {
        String experienceStr = experiencesToString(experiences).toString();

        SystemMessage systemMessage = new SystemMessage("""
                너는 JSON 응답만 출력하는 AI야. 아래 클래스 형식에 맞춰 응답해. (```json ``` 포함 엄금)
                {
                  "structure": string,
                  "content": string,
                }
               반드시 JSON만 출력해. 설명이나 불필요한 텍스트 금지.""");

        String userString = String.format("%s\n%s\n", requestDto.toString(), experienceStr +
                "위 데이터를 분석하고, 주어진 경험 데이터를 참고하여 %d 분량의 자기소개서 문항을 작성해줘, 작성한 문항의 문단별로 정리하여 structure필드에 매핑하고, 문항은 content로 매핑하여 리턴해줘", requestDto.getLimit());

        UserMessage userMessage = new UserMessage(userString);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse chatResponse = openAiChatModel.call(prompt);

        String result = chatResponse.getResult().getOutput().getText();
        log.info("result : {}", result);
        return result;
    }

    private StringBuilder experiencesToString(List<Experience> experiences) {
        StringBuilder experienceString = new StringBuilder();
        experiences.forEach(experience -> {
            experienceString.append(experienceConverter.toText(experience));
            experienceString.append(experienceConverter.toText(experience.getSubExperiences()));
            experienceString.append("\n\n");
        });

        return experienceString;
    }

    private String detailRecruitToString() {
        StringBuilder recruits = new StringBuilder();
        detailRecruitRepository.findAll()
                .forEach(recruit -> recruits.append(", ").append(recruit.getName()));
        return recruits.toString();
    }
}
