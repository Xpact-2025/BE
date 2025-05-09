package com.itstime.xpact.global;

import com.itstime.xpact.domain.recruit.service.CoreSkillService;
import com.itstime.xpact.domain.recruit.service.DetailRecruitService;
import com.itstime.xpact.domain.recruit.service.RecruitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitData implements ApplicationRunner {

    private final RecruitService recruitService;
    private final DetailRecruitService detailRecruitservice;
    private final CoreSkillService coreSkillService;

    @Override
    public void run(ApplicationArguments args) {

        if(!checkRecruitData()) recruitService.saveRecruit();
        else log.info("Recruit Column has already been initialized");

        if(!checkDetailRecruitData()) detailRecruitservice.saveDetailRecruit();
        else log.info("DetailRecruit Column has already been initialized");

        if(!checkCoreSkillData()) coreSkillService.saveCoreSkillData();
        else log.info("CoreSkill Column has already been initialized");
    }

    private boolean checkRecruitData() {
        return recruitService.recruitExists();
    }

    private boolean checkDetailRecruitData() {
        return detailRecruitservice.detailRecruitExists();
    }

    private boolean checkCoreSkillData() { return coreSkillService.coreSkillExists();}
}
