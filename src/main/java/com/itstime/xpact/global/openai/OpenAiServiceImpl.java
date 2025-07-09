package com.itstime.xpact.global.openai;

import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.entity.SubExperience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    private final OpenAiChatModel openAiChatModel;

    private final DetailRecruitRepository detailRecruitRepository;
    private final ExperienceRepository experienceRepository;

    @Async
    @Transactional
    public void summarizeExperience(Experience experience, List<SubExperience> subExperiences) {
        String message = String.format(
                """
                    다음 주어질 경험에 대해 역할, 내가 한 일, 성과(결과)가 드러나도록 2줄 분량으로 요약해줘
                    요약만 출력되도록 해줘
                    Experience : %s
                    SubExperience : %s
                """,
                experience.toString(), subExperiences.toString());

        System.out.println("prompt input value = " + message);
        Prompt prompt = new Prompt(message);
        ChatResponse response = openAiChatModel.call(prompt);
        String summary = response.getResult().getOutput().getText();
        log.info("summary : {}", summary);

        Experience fresh = experienceRepository.findById(experience.getId()).orElseThrow();
        fresh.setSummary(summary);
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

            String promptMessage = format(
                    """
                            다음 ','로 구분된 직무에 대해, 각 직무에서 실제로 요구되는 핵심 스킬 5가지를 도출해줘.
                            
                            직무별 핵심 스킬은 채용 공고나 직무 정의를 기반으로 현실적으로 중요한 것들을 중심으로 선정해줘.
                            각 직무마다 정확히 5개 스킬을 도출하고, 중요도 순으로 나열해.
                            유사하거나 모호한 표현 대신 구체적인 기술명이나 역량명을 써줘.
                            
                            출력 형식은 다음과 같아:
                            {직무}-{핵심스킬1}/{핵심스킬2}/{핵심스킬3}/{핵심스킬4}/{핵심스킬5}
                            단, 직무 이름은 절대로 변형하지 말고, 하이픈(-)과 슬래시(/)는 반드시 포함해.
                            
                            예시:
                            서비스 기획자-사용자 중심 사고/커뮤니케이션/문제 해결력/데이터 분석/콘텐츠 기획력
                            데이터 엔지니어-SQL/데이터 파이프라인/ETL/클라우드 플랫폼/시스템 최적화
                            펀드매니저-재무 분석/투자 전략 수립/위험 관리/시장 트렌드 분석/포트폴리오 관리
                            안전관리자-산업안전 지식/위험 분석/안전 점검 능력/법규 이해/커뮤니케이션
                            
                            
                            직무 : %s""", joinedDetailNames);

            Prompt prompt = new Prompt(promptMessage);
            ChatResponse response = openAiChatModel.call(prompt);
            String responseText = response.getResult().getOutput().getText();
            System.out.println("responseText = " + responseText);

            Arrays.stream(responseText.split("\n")).forEach(string -> {
                String[] row = string.split("-");
                coreSkillsForDetails.put(row[0], row[1].trim());
            });
            result.put(recruitName, coreSkillsForDetails);
        }

        return result;
    }

    @Transactional
    public void getDetailRecruitFromExperience(Experience experience) {
        String experienceStr = experience.toString();
        Map<String, DetailRecruit> detailRecruitMap = detailRecruitRepository.findAll().stream()
                .collect(Collectors.toMap(DetailRecruit::getName, Function.identity()));

        String message = String.format(
                """     
                        다음 경험을 분석해서 주어진 recruit 중 가장 적절한 하나를 선택해 주세요.
                        경험: %s
                        recruit (각 recruit는 ', '로 분리되어 있음) : %s
                        출력 형식 : recruit
                        출력 시 다른 문구 넣지 말고 그저 선택한 recruit만 출력해야함
                """,
                experienceStr, detailRecruitMap.values().stream().map(DetailRecruit::getName).collect(Collectors.joining(", "))
        );

        Prompt prompt = new Prompt(message);
        ChatResponse response = openAiChatModel.call(prompt);
        String result = response.getResult().getOutput().getText();
        log.info("result : {}", result);
        DetailRecruit detailRecruit = detailRecruitMap.get(result);
        Experience fresh = experienceRepository.findById(experience.getId()).orElseThrow(() ->
                GeneralException.of(ErrorCode.EXPERIENCE_NOT_EXISTS));
        fresh.setDetailRecruit(detailRecruit);
    }

    @Async
    public CompletableFuture<String> analysisWeakness(String weakness, String experiences) {

        StringBuilder builder = new StringBuilder();
        builder.append("다음은 나의 경험들에 대한 요약본이다.\n\n");
        builder.append(experiences).append("\n");
        builder.append("이에 대하여 나의 약점에 대한 원인 분석을 해주고, 피드백을 해줘. 나의 약점은 ").append(weakness).append("이다.\n");

        PromptTemplate template = new PromptTemplate(String.valueOf(builder));

        String message = template.render();

        Message userMessage = new UserMessage(message);
        Message systemMessage = new SystemMessage("350~400 byte 분량으로 답하고 줄바꿈은 없다. 존댓말을 사용해라.");

        String result = openAiChatModel.call(systemMessage, userMessage);

        return CompletableFuture.completedFuture(result);
    }

    // 3가지 약점 분석 -> 3가지 약점에 맞춘 맞춤형 활동 추천하기
    public List<String> getRecommendActivities(String weakness) {

        String systemString = String.join(" ",
                "너는 입력된 키워드를 기반으로 의미적으로 관련된 직무, 기술, 산업, 직군 키워드를 추천해주는 전문가다.",
                "추천 키워드는 실제 대외활동이나 채용 공고 등에서 자주 등장하는 실용적인 키워드여야 한다.",
                "한국어 키워드 7개와 영어 키워드 3개로, 총 10개의 키워드를 쉼표(,)로 구분해서 출력해라.",
                "다른 설명이나 문장은 절대 포함하지 마.",
                "예: 해커톤, 데이터베이스, 서버 운영, 웹 개발, 클라우드, 정보보안, 데이터 분석, backend, database, cloud"
        );
        String userString = String.format("%s", weakness);

        Message userMessage = new UserMessage(userString);
        Message systemMessage = new SystemMessage(systemString);

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse response = openAiChatModel.call(prompt);

        String result = response.getResult().getOutput().getText();
        log.info("result : {}", result);
        return Arrays.stream(result.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
