package com.itstime.xpact.domain.dashboard.service;

import com.itstime.xpact.domain.dashboard.dto.response.RatioResponseDto;
import com.itstime.xpact.domain.dashboard.entity.RecruitCount;
import com.itstime.xpact.domain.dashboard.repository.RecruitCountRepository;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.domain.recruit.entity.CoreSkill;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.openai.OpenAiService;
import com.itstime.xpact.global.response.RestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final OpenAiService openAiService;
    private final SecurityProvider securityProvider;
    private final ScoreResultStore scoreResultStore;

    private final MemberRepository memberRepository;
    private final DetailRecruitRepository detailRecruitRepository;
    private final ExperienceRepository experienceRepository;
    private final RecruitCountRepository recruitCountRepository;

    @Transactional(readOnly = true)
    public CompletableFuture<String> coreSkillMap() throws CustomException {

        Long memberId = securityProvider.getCurrentMemberId();
        log.info("{} 회원 조회 시작...", memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));

        String desiredRecruit = member.getDesiredRecruit();
        log.info("{} DetailRecruit 조회 시작...", desiredRecruit);
        DetailRecruit detailRecruit = detailRecruitRepository.findByName(desiredRecruit)
                .orElseThrow(() -> CustomException.of(ErrorCode.DETAILRECRUIT_NOT_FOUND));

        log.info("{} DetailRecruit에 맞는 CoreSkill 조회 시작...", detailRecruit.getName());
        CoreSkill coreSkill = detailRecruitRepository.findCoreSkillById(detailRecruit.getId())
                .orElseThrow(() -> CustomException.of(ErrorCode.NOT_FOUND_CORESKILLS));

        String experiences = experienceRepository.findSummaryByMemberId(memberId).stream()
                .collect(Collectors.joining("\n"));

        List<String> coreSkillList = coreSkill.getCoreSKills();
        return openAiService.evaluateScore(experiences, coreSkillList)
                .thenApply(result -> {
                    scoreResultStore.save(memberId, result);
                    return result;
                });
    }

    // 직무 카운트 값을 가져와 직무 비율로 변환하는 로직
    public RatioResponseDto detailRecruitRatio() {
        // 직무 카운트 값 가져옴
        Map<String, Integer> ratios = getCounts();

        // 해당 카운트의 합 구함
        int sum = ratios
                .values()
                .stream()
                .mapToInt(Integer::valueOf)
                .sum();

        // entry의 value에 대해 sum으로 나눈 값(비율) 리턴
        Map<String, Double> result = ratios.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Math.round(entry.getValue() / (double) sum * 1000) / 10.0,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // 비율이 각각 33.3, 33.3 33.3일 경우 합이 100이 안되는 경우 존재
        // adjustRatio()에서 이를 보정 (100에서 합을 빼고 가장 value가 큰 entry에 해당 차를 더함)
        adjustRatio(result);

        return RatioResponseDto.builder()
                .ratios(result)
                .build();
    }

    /**
     * 직무 비율을 redis에서 가져오는 메서드
     * 레디스에 없을 시, 직무 비율 새로 업데이트 진행 (setRatios())
     */
    private Map<String, Integer> getCounts() {
        RecruitCount recruitCount = recruitCountRepository.findById(securityProvider.getCurrentMemberId())
                .orElseGet(this::setCounts);

        log.info("get count from redis");
        return recruitCount.getRecruitCount().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue()
                        .reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(4)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 모든 경험을 fetch하여 각 관련된 상세직무의 카운트를 구해 redis에 저장
     */
    private RecruitCount setCounts() {
        Long memberId = securityProvider.getCurrentMemberId();

        List<Experience> experiences = experienceRepository.findAllWithDetailRecruitByMemberId(memberId);
        Map<String, Integer> result = new HashMap<>();
        experiences.forEach(e -> {
                    String detailRecruit = e.getDetailRecruit().getName();
                    if(detailRecruit != null) {
                        result.put(detailRecruit, result.getOrDefault(detailRecruit, 0) + 1);
                    }
                });

        RecruitCount recruitCount = RecruitCount.builder().id(memberId).recruitCount(result).build();
        recruitCountRepository.save(recruitCount);
        log.info("new count was saved");
        return recruitCount;
    }

    /**
     * 비율의 합이 100이 아닐 경우 합이 100이 되도록 보정해줌
      */
    private void adjustRatio(Map<String, Double> result) {
        double diff = Math.round((100.0 - result.values().stream().mapToDouble(Double::doubleValue).sum()) * 10) / 10.0;
        System.out.println("diff = " + diff);

        if(diff > 0) {
            String maxKey = result.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElseThrow(() -> CustomException.of(ErrorCode.INTERNAL_SERVER_ERROR))
                    .getKey();

            result.put(maxKey, result.get(maxKey) + diff);
        } else if(diff < 0) {
            String minKey = result.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .orElseThrow(() -> CustomException.of(ErrorCode.INTERNAL_SERVER_ERROR))
                    .getKey();

            result.put(minKey, result.get(minKey) - diff);
        }
    }

    public void refreshData() {
        checkSummaryOfExperience();
        checkDetailRecruitOfExperience();
    }

    public void checkSummaryOfExperience() {
        List<Experience> experiences = experienceRepository.findAllWithKeywordByMemberId(securityProvider.getCurrentMemberId());

        experiences.stream()
            .filter(e -> e.getSummary() == null || e.getSummary().isEmpty())
            .forEach(openAiService::summarizeExperience);
    }

    public void checkDetailRecruitOfExperience() {
        List<Experience> experiences = experienceRepository.findAllWithKeywordByMemberId(securityProvider.getCurrentMemberId());
        experiences.stream()
                .filter(e -> e.getDetailRecruit() == null)
                .forEach(openAiService::getDetailRecruitFromExperience);
    }
}
