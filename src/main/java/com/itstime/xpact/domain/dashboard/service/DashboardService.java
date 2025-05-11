package com.itstime.xpact.domain.dashboard.service;

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

import java.util.List;
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
}
