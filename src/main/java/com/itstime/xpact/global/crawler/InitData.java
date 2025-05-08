package com.itstime.xpact.global.crawler;

import com.itstime.xpact.domain.recruit.service.CoreSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
@RequiredArgsConstructor
public class InitData implements ApplicationRunner {

    private final CrawlerService crawlerService;
    private final CoreSkillService coreSkillService;

    @Override
    public void run(ApplicationArguments args) {
        if(!checkRecruitData()) crawlerService.saveRecruitData();
        else log.info("Recruit Column has already been initialized");

        if(!checkDetailRecruitData()) crawlerService.saveDetailRecruitData();
        else log.info("DetailRecruit Column has already been initialized");

        if(!checkCoreSkillData()) coreSkillService.saveCoreSkillData();
        else log.info("CoreSkill Column has already been initialized");
    }

    private boolean checkRecruitData() {
        return crawlerService.recruitExists();
    }

    private boolean checkDetailRecruitData() {
        return crawlerService.detailRecruitExists();
    }

    private boolean checkCoreSkillData() { return coreSkillService.coreSkillExists();}
}
