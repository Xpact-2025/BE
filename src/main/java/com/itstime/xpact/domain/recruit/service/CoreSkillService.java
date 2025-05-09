package com.itstime.xpact.domain.recruit.service;

import com.itstime.xpact.domain.recruit.entity.CoreSkill;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import com.itstime.xpact.domain.recruit.repository.CoreSkillRepository;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.domain.recruit.repository.RecruitRepository;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoreSkillService {

    private final OpenAiService openAiService;

    private final CoreSkillRepository coreSkillRepository;
    private final DetailRecruitRepository detailRecruitRepository;
    private final RecruitRepository recruitRepository;

    public boolean existsCoreSkill() {
        return coreSkillRepository.count() > 0;
    }

    @Transactional
    public void saveCoreSkillData() {
        List<String> recruitNames = recruitRepository.findAllNames();
        Map<String, Map<String, String>> coreSkillsOfRecruit = openAiService.getCoreSkill(recruitNames);
        List<CoreSkill> coreSkills = new ArrayList<>();

        // Map<String, Map<String, String>> : <기획 , <서비스기획자, 사용자 중심 사고/콘텐츠 기획력/커뮤니케이션/문제 해결력/데이터 분석>>
        // '/'로 split하여 skillCore로 저장
        for (Map.Entry<String, Map<String, String>> entry : coreSkillsOfRecruit.entrySet()) {
            String recruitName = entry.getKey();
            Map<String, String> detailSkillMap = entry.getValue();

            for (Map.Entry<String, String> detailEntry : detailSkillMap.entrySet()) {
                String detailRecruitName = detailEntry.getKey();
                String skills = detailEntry.getValue();

                if (skills == null || skills.isBlank()) {
                    log.warn("Core skills not found for detail recruitId: {} under recruitName: {}", detailRecruitName, recruitName);
                    continue;
                }

                List<String> coreSkillList = Arrays.stream(skills.split("/"))
                        .map(String::trim)
                        .toList();

                CoreSkill coreSkill = new CoreSkill(coreSkillList);
                DetailRecruit detailRecruit = detailRecruitRepository.findByName(detailRecruitName)
                        .orElseThrow(() -> {
                            log.warn("DetailRecruit 조회 실패 : {}", detailRecruitName);
                            return CustomException.of(ErrorCode.DETAILRECRUIT_NOT_FOUND);
                        });
                detailRecruit.setCoreSkill(coreSkill);
                coreSkills.add(coreSkill);
            }
        }
        log.info("Successfully Save CoreSkill Data");
        log.info("coreSkills size: {}", coreSkills.size());
        log.info("Successfully Set CoreSkill relationships to Recruits");
    }
}
