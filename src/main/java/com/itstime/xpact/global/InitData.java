package com.itstime.xpact.global;

import com.itstime.xpact.domain.recruit.service.CoreSkillService;
import com.itstime.xpact.domain.recruit.service.DetailRecruitService;
import com.itstime.xpact.domain.recruit.service.RecruitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitData {

    private final RecruitService recruitService;
    private final DetailRecruitService detailRecruitservice;
    private final CoreSkillService coreSkillService;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {

        try {
            if (!checkRecruitData()) {
                recruitService.saveRecruit();
            } else {
                log.info("Recruit Column has already been initialized");
            }

            if (!checkDetailRecruitData()) {
                detailRecruitservice.saveDetailRecruit();
            } else {
                log.info("DetailRecruit Column has already been initialized");
            }

            if (!checkCoreSkillData()) {
                coreSkillService.saveCoreSkillData();
            } else {
                log.info("CoreSkill Column has already been initialized");
            }
        } catch (Exception e) {
            log.error("Error occurred during InitData execution: {}", e.getMessage(), e);
        }
    }

    private boolean checkRecruitData() {
        return recruitService.recruitExists();
    }

    private boolean checkDetailRecruitData() {
        return detailRecruitservice.detailRecruitExists();
    }

    private boolean checkCoreSkillData() { return coreSkillService.coreSkillExists();}
}
