package com.itstime.xpact.domain.dashboard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.domain.dashboard.dto.response.MapResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.RatioResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.TimelineResponseDto;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    private final ObjectMapper objectMapper;

    private final DetailRecruitRepository detailRecruitRepository;
    private final ExperienceRepository experienceRepository;
    private final RecruitCountRepository recruitCountRepository;
    private final MemberRepository memberRepository;

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
                .thenAccept(resultDto -> {
                    try {
                        scoreResultStore.save(member.getId(), objectMapper.writeValueAsString(resultDto));
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
            return objectMapper.readValue(result, MapResponseDto.class);
        } catch (JsonProcessingException e) {
            log.error("OpenAI 응답 중 오류... {}", result, e);
            throw CustomException.of(ErrorCode.UNMATCHED_OPENAI_FORMAT);
        }
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

    @Transactional(readOnly = true)
    public List<TimelineResponseDto> getTimeline(
            LocalDate startLine, LocalDate endLine) {

        Long memberId = securityProvider.getCurrentMemberId();

        // Fetch Join을 통한 LazyInitializationException 방지
        Member member = memberRepository.findByIdWithExperiences(memberId)
                .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));

        return member.getExperiences().stream()
                .filter(experience ->
                {
                    LocalDate expStart = experience.getStartDate();
                    LocalDate expEnd = experience.getEndDate();

                    if (expStart == null) return false; // 시작일자 없는 것은 제외

                    return (expEnd == null || !expEnd.isBefore(startLine)) && (!expStart.isAfter(endLine) && (expStart.isAfter(startLine)));
                })
                .sorted(Comparator.comparing(Experience::getStartDate))
                .map(Experience::toTimeLineDto)
                .collect(Collectors.toList());
    }
}
