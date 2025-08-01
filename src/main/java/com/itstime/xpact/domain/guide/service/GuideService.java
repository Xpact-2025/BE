package com.itstime.xpact.domain.guide.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.domain.dashboard.dto.response.SkillMapResponseDto;
import com.itstime.xpact.domain.dashboard.entity.CoreSkillMap;
import com.itstime.xpact.domain.dashboard.repository.CoreSkillMapRepository;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.guide.dto.response.ScrapDetailResponseDto;
import com.itstime.xpact.domain.guide.dto.response.ScrapThumbnailResponseDto;
import com.itstime.xpact.domain.guide.dto.response.WeaknessGuideResponseDto;
import com.itstime.xpact.domain.guide.entity.MemberScrap;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideService {

    private final ObjectMapper objectMapper;
    private final SecurityProvider securityProvider;
    private final OpenAiService openAiService;

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
                    weaknessRepository.save(existing);
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
    public Slice<ScrapThumbnailResponseDto> getActivities(int weaknessOrder, Pageable pageable) {
        Member member = securityProvider.getCurrentMember();

        // OpenAI로부터 추천 키워드 받기
        List<String> keywords = (weaknessOrder == 0)
                ? getKeywordsForAll(member) : getKeywords(member, weaknessOrder);

        // 추천 기반 공고 조회
        Slice<Scrap> scrapList = scrapRepository.findByTitleContainingKeywords(keywords, pageable);
        List<Long> scrapIdList = scrapList.stream()
                .map(Scrap::getId)
                .toList();

        // 스크랩 여부 조회
        List<MemberScrap> memberScrapList = memberScrapRepository.findByMemberAndScrapIds(member, scrapIdList);
        Set<Long> scrappedScrapIdList = memberScrapList.stream()
                .map(MemberScrap::getScrap)
                .filter(scrap -> {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                        LocalDate endDate = LocalDate.parse(scrap.getEndDate(), formatter);
                        return !endDate.isBefore(LocalDate.now());
                    } catch (DateTimeParseException ignored) { // endDate에 '채용 마감 시'같은 date가 아닌 string이 있을 때
                        return false;
                    }
                })
                .map(Scrap::getId)
                .collect(Collectors.toSet());

        return scrapList
                .map(s -> {
                    Boolean isScraped = scrappedScrapIdList.contains(s.getId());
                    return ScrapThumbnailResponseDto.of(s, isScraped);
                });
    }



    // WeaknessOrder = 0 일 때
    private List<String> getKeywordsForAll(Member member) {
        List<String> weaknessNames = weaknessRepository.findByMemberId(member.getId())
                .stream()
                .map(Weakness::getName)
                .filter(name -> name != null && !name.isBlank())
                .toList();

        Set<String> keywordSet = new HashSet<>();

        for (String weaknessName : weaknessNames) {
            List<String> keywordsByAi = openAiService.getRecommendActivities(weaknessName);
            keywordSet.addAll(keywordsByAi);
        }
        return new ArrayList<>(keywordSet);
    }

    // WeaknessOrder != 0 일 때
    private List<String> getKeywords(Member member, int weaknessOrder) {
        List<Weakness> weaknesses = weaknessRepository.findByMemberId(member.getId());
        System.out.println("weaknesses = " + weaknesses);
        String weakness = weaknesses.get(weaknessOrder - 1)
                .getName();

        // 만약 약점 분석이 안 되어있을 경우 우선적으로 분석 필수
        if (weakness == null || weakness.trim().isEmpty()) {
            throw CustomException.of(ErrorCode.NEED_ANALYSIS);
        }

        return openAiService.getRecommendActivities(weakness);
    }

    public ScrapDetailResponseDto getActivity(Long scrapId) {
        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(() ->
                GeneralException.of(ErrorCode.SCRAP_NOT_EXISTS));

        List<String> jobCategory = parsingJobCategory(scrap);

        return ScrapDetailResponseDto.of(scrap, jobCategory);
    }

    private List<String> parsingJobCategory(Scrap scrap) {
        List<String> jobCategory;

        if (scrap.getJobCategory() == null || scrap.getJobCategory().trim().isEmpty()) {
            return List.of(); // 빈 리스트 반환
        }

        try {
            jobCategory = objectMapper.readValue(scrap.getJobCategory(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            // 파싱 실패 시: 개행, 콤마, 또는 둘 다로 나눈 후 trim
            jobCategory = Arrays.stream(scrap.getJobCategory()
                            .split("[,\n\r]")) // 쉼표, 개행 문자 모두 기준
                    .map(String::trim)
                    .filter(s -> !s.isEmpty()) // 빈 문자열 제거
                    .toList();
        }
        return jobCategory;
    }
}