package com.itstime.xpact.domain.dashboard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.domain.dashboard.dto.response.MapResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.ScoreResponseDto;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.recruit.entity.CoreSkill;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final OpenAiService openAiService;
    private final SecurityProvider securityProvider;
    private final ScoreResultStore scoreResultStore;
    private final ObjectMapper objectMapper;

    private final DetailRecruitRepository detailRecruitRepository;
    private final ExperienceRepository experienceRepository;


    @Transactional(readOnly = true)
    public Long evaluateAsync() throws CustomException {
        Member member = securityProvider.getCurrentMember();

        CompletableFuture.runAsync(() -> {
            try {
                performEvaluation(member);
            } catch (CustomException e) {
                log.error("Request Not Accepted.", e.getMessage());
            }
        });
        return member.getId();
    }

    private void performEvaluation(Member member) throws CustomException {

        String desiredRecruit = member.getDesiredRecruit();
        log.info("{} DetailRecruit 조회 시작...", desiredRecruit);
        DetailRecruit detailRecruit = detailRecruitRepository.findByName(desiredRecruit)
                .orElseThrow(() -> CustomException.of(ErrorCode.DETAILRECRUIT_NOT_FOUND));

        log.info("{} DetailRecruit에 맞는 CoreSkill 조회 시작...", detailRecruit.getName());
        CoreSkill coreSkill = detailRecruitRepository.findCoreSkillById(detailRecruit.getId())
                .orElseThrow(() -> CustomException.of(ErrorCode.NOT_FOUND_CORESKILLS));

        String experiences = experienceRepository.findSummaryByMemberId(member.getId()).stream()
                .collect(Collectors.joining("\n"));

        List<String> coreSkillList = coreSkill.getCoreSKills();

        openAiService.evaluateScore(experiences, coreSkillList)
                .thenApply(this::toMapDto)
                .thenAccept(resultDto -> {
                    try {
                        scoreResultStore.save(member.getId(), resultDto.toString());
                    } catch (Exception e) {
                        log.error("결과를 저장하는 것에 실패하였습니다.", e);
                    }
                });
    }

    public Optional<MapResponseDto> getEvaluationResult(Long memberId) {
        return Optional.of(toMapDto(scoreResultStore.get(memberId)));
    }

    // 점수를 DTO로 매핑
    private MapResponseDto toMapDto(String result) {

        try {
            Map<String, Double> maps = objectMapper.readValue(result, new TypeReference<>() {});

            List<ScoreResponseDto> scores = maps.entrySet()
                    .stream()
                    .map(entry -> new ScoreResponseDto(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

            return new MapResponseDto(scores);
        } catch (JsonProcessingException e) {
            log.error("OpenAI 응답 중 오류... {}", result, e);
            throw CustomException.of(ErrorCode.UNMATCHED_OPENAI_FORMAT);
        }
    }
}
