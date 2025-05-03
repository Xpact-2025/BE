package com.itstime.xpact.domain.recruit.service;

import com.itstime.xpact.domain.recruit.entity.CoreSkill;
import com.itstime.xpact.domain.recruit.entity.Recruit;
import com.itstime.xpact.domain.recruit.repository.CoreSkillRepository;
import com.itstime.xpact.domain.recruit.repository.RecruitRepository;
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
    private final RecruitRepository recruitRepository;

    public boolean existsCoreSkill() {
        return coreSkillRepository.count() > 0;
    }

    @Transactional
    public void saveCoreSkillData() {
        List<Recruit> recruits = recruitRepository.findAll();
        Map<String, String> coreSkillsOfRecruit = openAiService.getCoreSkill(recruits.stream().map(Recruit::getName).toList());
        List<CoreSkill> coreSkills = new ArrayList<>();

        // coreSkillsOfRecruit["AI·개발·데이터"] = 문제 해결 능력/프로그래밍 역량/데이터 이해 및 활용/시스템 설계/도메인 지식 기반의 분석력
        // '/'로 split하여 skillCore로 저장
        for (Recruit recruit : recruits) {
            List<String> coreSkillList = Arrays.stream(coreSkillsOfRecruit.get(recruit.getName()).split("/")).toList();
            CoreSkill coreSkill = new CoreSkill(coreSkillList);
            coreSkills.add(coreSkill);
            recruit.setCoreSkill(coreSkill);
            coreSkillRepository.save(coreSkill);
        }

        log.info("Successfully Save CoreSkill Data");
        log.info("Successfully Set CoreSkill relationships to Recruits");
    }
}
