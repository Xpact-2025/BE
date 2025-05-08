package com.itstime.xpact.domain.recruit;

import com.itstime.xpact.domain.recruit.service.CoreSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitCoreSkillData implements ApplicationRunner {

    private final CoreSkillService coreSkillService;

    public void run(ApplicationArguments args) {
        if(!checkCoreSkillData()) coreSkillService.saveCoreSkillData();
        else log.info("CoreSKill Column has already been initialized");
    }

    private boolean checkCoreSkillData() {
        return coreSkillService.existsCoreSkill();
    }
}
