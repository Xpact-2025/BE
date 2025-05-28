package com.itstime.xpact.domain.dashboard.service.skillmap;

import com.itstime.xpact.domain.dashboard.dto.response.SkillMapResponseDto;
import com.itstime.xpact.domain.dashboard.entity.CoreSkillMap;
import com.itstime.xpact.domain.dashboard.repository.CoreSkillMapRepository;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.recruit.entity.CoreSkill;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.webclient.openai.LambdaOpenAiClient;
import com.itstime.xpact.global.webclient.openai.OpenAiRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SkillmapService {

    private final DetailRecruitRepository detailRecruitRepository;
    private final ExperienceRepository experienceRepository;
    private final CoreSkillMapRepository coreSkillMapRepository;

    private final LambdaOpenAiClient lambdaOpenAiClient;

    /*
    사용자의 모든 경험 요약과, 사용자의 희망 직무에대한 다섯가지 핵심 역량을 입력값으로 사용하여
    각 다섯가지 역량에 맞는 점수를 매기고, AWS람다를 호출하여 분석을 진행
    결과값은 redis에 저장하여, 대시보드 새로고침 시 연산이 반복되지 않도록 방지
     */
    public CompletableFuture<SkillMapResponseDto> evaluate(Member member) {

        // Redis 캐시 먼저 조회
        Optional<CoreSkillMap> cachedCoreSkillMap = coreSkillMapRepository.findById(member.getId());
        if (cachedCoreSkillMap.isPresent()) {
            log.info("Redis에 저장된 CoreSkillMap 조회 성공: {}", member.getId());
            return CompletableFuture.completedFuture(
                    cachedCoreSkillMap.get().getSkillMapDto()
            );
        }

        // Redis 캐시에 없을 경우
        String desiredRecruit = member.getDesiredRecruit();
        log.info("{} DetailRecruit 조회 시작...", desiredRecruit);
        DetailRecruit detailRecruit = detailRecruitRepository.findByName(desiredRecruit)
                .orElseThrow(() -> CustomException.of(ErrorCode.DETAIL_RECRUIT_NOT_FOUND));

        log.info("{} DetailRecruit에 맞는 CoreSkill 조회 시작...", detailRecruit.getName());
        CoreSkill coreSkill = detailRecruitRepository.findCoreSkillById(detailRecruit.getId())
                .orElseThrow(() -> CustomException.of(ErrorCode.NOT_FOUND_CORESKILLS));

        // 사용자의 모든 경험의 요약을 한줄씩 만듭니다.
        String experiences = experienceRepository.findSummaryByMemberId(member.getId()).stream()
                .collect(Collectors.joining("\n"));

        // 사용자의 희망직무의 핵심역량을 조회하여 List형태로 받습니다.
        List<String> coreSkillList = coreSkill.getCoreSKills();

        // experiences가 null이거나 비어있는 경우 예외 처리
        if (experiences == null || experiences.trim().isEmpty()) {
            throw CustomException.of(ErrorCode.EXPERIENCES_NOT_ENOUGH);
        }

        // 경험 요약과 핵심역량으로 보낼 요청 dto를 생성합니다.
        OpenAiRequestDto requestDto = OpenAiRequestDto.builder()
                .experiences(experiences)
                .coreSkills(coreSkillList)
                .build();

        /*
        lambda를 호출하여 openAi요청을 처리함
        */
        return lambdaOpenAiClient.requestEvaluation(requestDto)
                .thenApply(resultDto -> {
                    CoreSkillMap coreSkillMap = CoreSkillMap.builder()
                            .id(member.getId())
                            .skillMapDto(resultDto).build();
                    coreSkillMapRepository.save(coreSkillMap);
                    return resultDto;
                });
    }
}
