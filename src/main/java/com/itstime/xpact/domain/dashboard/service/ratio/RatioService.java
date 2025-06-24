package com.itstime.xpact.domain.dashboard.service.ratio;

import com.itstime.xpact.domain.dashboard.dto.response.RatioResponseDto;
import com.itstime.xpact.domain.dashboard.entity.RecruitCount;
import com.itstime.xpact.domain.dashboard.repository.RecruitCountRepository;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatioService {

    private final RecruitCountRepository recruitCountRepository;
    private final ExperienceRepository experienceRepository;

    // 직무 카운트 값을 가져와 직무 비율로 변환하는 로직
    public List<RatioResponseDto> detailRecruitRatio(Long memberId) {
        // 직무 카운트 값 가져옴
        Map<String, Integer> ratios = getCounts(memberId);

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
                        LinkedHashMap::new));

        // 비율이 각각 33.3, 33.3 33.3일 경우 합이 100이 안되는 경우 존재
        // adjustRatio()에서 이를 보정 (100에서 합을 빼고 가장 value가 큰 entry에 해당 차를 더함)
        adjustRatio(result);

        return result.entrySet()
                .stream()
                .map(RatioResponseDto::of)
                .toList();
    }

    /**
     * 직무 비율을 redis에서 가져오는 메서드
     * 레디스에 없을 시, 직무 비율 새로 업데이트 진행 (setRatios())
     */
    private Map<String, Integer> getCounts(Long memberId) {
        RecruitCount recruitCount = recruitCountRepository.findById(memberId)
                .orElseGet(() -> setCounts(memberId));

        if(recruitCount.getRecruitCount().isEmpty()) throw GeneralException.of(ErrorCode.NO_EXPERIENCE);

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
    private RecruitCount setCounts(Long memberId) {

        List<Experience> experiences = experienceRepository.findAllWithDetailRecruitByMemberId(memberId);

        if(experiences == null || experiences.isEmpty()) {
            throw GeneralException.of(ErrorCode.EXPERIENCES_NOT_ENOUGH);
        }

        Map<String, Integer> result = new HashMap<>();
        experiences.forEach(e -> {
            if(e.getDetailRecruit() != null) {
                String detailRecruit = e.getDetailRecruit().getName();
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
        if(diff > 0) {
            String maxKey = result.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElseThrow(() -> GeneralException.of(ErrorCode.EXPERIENCES_NOT_ENOUGH))
                    .getKey();

            result.put(maxKey, result.get(maxKey) + diff);
        } else if(diff < 0) {
            String minKey = result.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .orElseThrow(() -> GeneralException.of(ErrorCode.EXPERIENCES_NOT_ENOUGH))
                    .getKey();

            result.put(minKey, result.get(minKey) - diff);
        }
    }
}
