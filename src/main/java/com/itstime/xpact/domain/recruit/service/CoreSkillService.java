package com.itstime.xpact.domain.recruit.service;

import com.itstime.xpact.domain.recruit.entity.CoreSkill;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import com.itstime.xpact.domain.recruit.repository.CoreSkillRepository;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
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

    public boolean existsCoreSkill() {
        return coreSkillRepository.count() > 0;
    }

    @Transactional
    public void saveCoreSkillData() {
        List<DetailRecruit> detailRecruits = detailRecruitRepository.findAll();
        Map<String, String> coreSkillsOfRecruit = openAiService.getAllCoreSkillsInBatch(detailRecruits.stream().map(DetailRecruit::getName).toList());
        List<CoreSkill> coreSkills = new ArrayList<>();

        // coreSkillsOfRecruit["서비스 기획자"] = 사용자 중심 사고/콘텐츠 기획력/커뮤니케이션/문제 해결력/데이터 분석
        // '/'로 split하여 skillCore로 저장
        for (DetailRecruit detailRecruit : detailRecruits) {
            List<String> coreSkillList = Arrays.stream(coreSkillsOfRecruit.get(detailRecruit).split("/")).toList();
            CoreSkill coreSkill = new CoreSkill(coreSkillList);
            coreSkills.add(coreSkill);
            detailRecruit.setCoreSkill(coreSkill);
        }

        coreSkillRepository.saveAll(coreSkills);
        log.info("Successfully Save CoreSkill Data");
        log.info("Successfully Set CoreSkill relationships to Recruits");
    }
}
