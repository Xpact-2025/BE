package com.itstime.xpact.domain.dashboard.service.skillmap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.domain.dashboard.dto.response.FeedbackResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.MapResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.ScoreResponseDto;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.recruit.entity.CoreSkill;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SkillmapService {

    private final OpenAiService openAiService;
    private final ScoreResultStore scoreResultStore;
    private final ObjectMapper objectMapper;

    private final DetailRecruitRepository detailRecruitRepository;
    private final ExperienceRepository experienceRepository;

    public CompletableFuture<MapResponseDto> performEvaluation(Member member) throws CustomException {

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

        // experiences가 null이거나 비어있는 경우 예외 처리
        if (experiences == null || experiences.trim().isEmpty()) {
            throw CustomException.of(ErrorCode.EXPERIENCES_NOT_ENOUGH);
        }

        return openAiService.evaluateScore(experiences, coreSkillList)
                .thenApply(resultDto -> {
                    try {
                        scoreResultStore.save(member.getId(), objectMapper.writeValueAsString(resultDto));
                        return analysisScore(member, resultDto);
                    } catch (Exception e) {
                        log.error("결과를 저장하는 것에 실패하였습니다.", e);
                        throw CustomException.of(ErrorCode.FAILED_GET_RESULT);
                    }
                });
    }

    // 점수가 낮은 것과 높은 것 고르기
    private MapResponseDto analysisScore(Member member, MapResponseDto dto) {
        List<ScoreResponseDto> skills = dto.getCoreSkillMaps();

        String minScoreSkill = skills.stream()
                .min(Comparator.comparingDouble(ScoreResponseDto::getScore))
                .map(ScoreResponseDto::getCoreSkillName)
                .orElse(null);
        scoreResultStore.saveWeakness(member.getId(), minScoreSkill);

        String maxScoreSkill = skills.stream()
                .max(Comparator.comparingDouble(ScoreResponseDto::getScore))
                .map(ScoreResponseDto::getCoreSkillName)
                .orElse(null);
        scoreResultStore.saveStrength(member.getId(), maxScoreSkill);

        if (minScoreSkill != null && maxScoreSkill != null) {
            dto.setAnalysis(maxScoreSkill, minScoreSkill);
        }
        return dto;
    }

    // 피드백에 대한 조회 - 강점
    public CompletableFuture<FeedbackResponseDto> getFeedbackStrength(Member member) throws CustomException {

        String experiences = experienceRepository.findSummaryByMemberId(member.getId()).stream()
                .collect(Collectors.joining("\n"));

        return openAiService.feedbackStrength(experiences, scoreResultStore.getStrength(member.getId()));
    }

    // 피드백에 대한 조회 - 약점
    public CompletableFuture<FeedbackResponseDto> getFeedbackWeakness(Member member) throws CustomException {

        String experiences = experienceRepository.findSummaryByMemberId(member.getId()).stream()
                .collect(Collectors.joining("\n"));

        return openAiService.feedbackWeakness(experiences, scoreResultStore.getWeakness(member.getId()));
    }
}
