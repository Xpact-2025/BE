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

        String experiences = experienceRepository.findSummaryByMemberId(member.getId()).stream()
                .collect(Collectors.joining("\n"));

        List<String> coreSkillList = coreSkill.getCoreSKills();

        // experiences가 null이거나 비어있는 경우 예외 처리
        if (experiences == null || experiences.trim().isEmpty()) {
            throw CustomException.of(ErrorCode.EXPERIENCES_NOT_ENOUGH);
        }

        OpenAiRequestDto requestDto = OpenAiRequestDto.builder()
                .experiences(experiences)
                .coreSkills(coreSkillList)
                .build();

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
