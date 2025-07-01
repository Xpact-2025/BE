package com.itstime.xpact.domain.guide.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.domain.dashboard.dto.response.SkillMapResponseDto;
import com.itstime.xpact.domain.dashboard.entity.CoreSkillMap;
import com.itstime.xpact.domain.dashboard.repository.CoreSkillMapRepository;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.guide.dto.ScrapThumbnailResponseDto;
import com.itstime.xpact.domain.guide.dto.WeaknessGuideResponseDto;
import com.itstime.xpact.domain.guide.entity.Scrap;
import com.itstime.xpact.domain.guide.entity.Weakness;
import com.itstime.xpact.domain.guide.repository.MemberScrapRepository;
import com.itstime.xpact.domain.guide.repository.ScrapRepository;
import com.itstime.xpact.domain.guide.repository.WeaknessRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideService {

    private final SecurityProvider securityProvider;
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    private final WeaknessRepository weaknessRepository;
    private final ExperienceRepository experienceRepository;
    private final CoreSkillMapRepository coreSkillMapRepository;
    private final ScrapRepository scrapRepository;
    private final MemberScrapRepository memberScrapRepository;

    @Async
    public CompletableFuture<Void> saveWeakness(Member member) {

        return CompletableFuture.runAsync(() -> {
            // 경험 분석 우선 필요
            CoreSkillMap coreSkillMap = coreSkillMapRepository.findById(member.getId())
                    .orElseThrow(() -> CustomException.of(ErrorCode.NEED_ANALYSIS));

            List<String> lowestThreeSkillNames = coreSkillMap.getSkillMapDto().getCoreSkillMaps()
                    .stream()
                    .sorted(Comparator.comparingDouble(SkillMapResponseDto.ScoreDto::getScore))
                    .limit(3)
                    .map(SkillMapResponseDto.ScoreDto::getCoreSkillName)
                    .toList();

            // weakness가 3개가 아니라면 재검사 필요
            if (lowestThreeSkillNames.size() != 3) {
                throw CustomException.of(ErrorCode.NEED_THREE_WEAKNESS);
            }

            // 기존의 Weakness 조회
            List<Weakness> existingWeakness = weaknessRepository.findByMemberId(member.getId());

            String experiences = experienceRepository.findSummaryByMemberId(member.getId()).stream()
                    .collect(Collectors.joining("\n"));

            if (experiences == null || experiences.trim().isEmpty()) {
                throw CustomException.of(ErrorCode.EXPERIENCES_NOT_ENOUGH);
            }

            List<CompletableFuture<String>> futures = lowestThreeSkillNames.stream()
                    .map(skillName -> openAiService.analysisWeakness(skillName, experiences))
                    .toList();

            List<String> explanations = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();

            // Weakness 기존에 존재하지 않을 때 새로 저장
            List<Weakness> savedWeakness = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                String newName = lowestThreeSkillNames.get(i);
                String explanation = explanations.get(i);

                if (i < existingWeakness.size()) {
                    // 존재할 경우
                    Weakness existing = existingWeakness.get(i);
                    existing.setName(newName);
                    existing.setExplanation(explanation);
                } else {
                    // 존재하지 않을 경우
                    savedWeakness.add(new Weakness(member, newName, explanation));
                    weaknessRepository.saveAll(savedWeakness);
                }
            }
                });
    }

    @Transactional(readOnly = true)
    public List<WeaknessGuideResponseDto> getAnalysis() {
        List<Weakness> weaknessList = weaknessRepository.findByMemberId(securityProvider.getCurrentMemberId());

        if (weaknessList.isEmpty()) {
            throw CustomException.of(ErrorCode.NEED_ANALYSIS);
        }

        return weaknessList
                .stream()
                .map(Weakness::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScrapThumbnailResponseDto> getActivities() {
        Member member = securityProvider.getCurrentMember();
        List<Weakness> weaknesses = weaknessRepository.findByMemberId(member.getId());
        String result = openAiService.getRecommendActivitiesByExperiecnes(weaknesses);

        List<Long> scrapResponseIds;
        try {
            scrapResponseIds = objectMapper.readValue(result, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw GeneralException.of(ErrorCode.FAILED_OPENAI_PARSING);
        }

        return scrapResponseIds.stream()
                .map(id -> {
                    Scrap scrap = scrapRepository.findById(id).orElseThrow(() ->
                            GeneralException.of(ErrorCode.SCRAP_NOT_EXISTS));
                    Boolean clipped = memberScrapRepository.existsByScrapAndMember(scrap, member);
                    return ScrapThumbnailResponseDto.of(scrap, clipped);
                })
                .toList();
    }
}
