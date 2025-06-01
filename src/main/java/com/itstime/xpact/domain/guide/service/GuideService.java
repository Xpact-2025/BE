package com.itstime.xpact.domain.guide.service;

import com.itstime.xpact.domain.dashboard.dto.response.SkillMapResponseDto;
import com.itstime.xpact.domain.dashboard.entity.CoreSkillMap;
import com.itstime.xpact.domain.dashboard.repository.CoreSkillMapRepository;
import com.itstime.xpact.domain.experience.repository.ExperienceRepository;
import com.itstime.xpact.domain.guide.dto.WeaknessGuideResponseDto;
import com.itstime.xpact.domain.guide.entity.Weakness;
import com.itstime.xpact.domain.guide.repository.WeaknessRepository;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideService {

    private final SecurityProvider securityProvider;
    private final OpenAiService openAiService;

    private final WeaknessRepository weaknessRepository;
    private final ExperienceRepository experienceRepository;
    private final CoreSkillMapRepository coreSkillMapRepository;

    @Transactional
    public void saveWeakness() {

        Member member = securityProvider.getCurrentMember();

        CoreSkillMap coreSkillMap = coreSkillMapRepository.findById(member.getId())
                .orElseThrow(() -> CustomException.of(ErrorCode.NEED_ANALYSIS));

        List<String> lowestThreeSkillNames = coreSkillMap.getSkillMapDto().getCoreSkillMaps()
                .stream()
                .sorted(Comparator.comparingDouble(SkillMapResponseDto.ScoreDto::getScore))
                .limit(3)
                .map(SkillMapResponseDto.ScoreDto::getCoreSkillName)
                .collect(Collectors.toList());

        weaknessRepository.deleteAllByMemberId(member.getId());

        List<Weakness> newWeaknesses = lowestThreeSkillNames.stream()
                .map(skillName -> {
                    Weakness weakness = new Weakness();
                    weakness.setMember(member);
                    weakness.setName(skillName);
                    return weakness;
                })
                .collect(Collectors.toList());

        weaknessRepository.saveAll(newWeaknesses);

        String experiences = experienceRepository.findSummaryByMemberId(member.getId()).stream()
                .collect(Collectors.joining("\n"));

        if (experiences == null || experiences.trim().isEmpty()) {
            throw CustomException.of(ErrorCode.EXPERIENCES_NOT_ENOUGH);
        }

        List<CompletableFuture<String>> futures = newWeaknesses.stream()
                .map(w -> openAiService.analysisWeakness(w.getName(), experiences))
                .collect(Collectors.toList());

        List<String> explanations = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        for (int i = 0; i < newWeaknesses.size(); i++) {
            newWeaknesses.get(i).setExplanation(explanations.get(i));
        }

        weaknessRepository.saveAll(newWeaknesses);
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
}
