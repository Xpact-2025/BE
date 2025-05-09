package com.itstime.xpact.domain.recruit;

import com.itstime.xpact.domain.recruit.service.CoreSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitCoreSkillData {

    private final CoreSkillService coreSkillService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (coreSkillService.existsCoreSkill()) {
            log.info("CoreSKill Column has already been initialized");
            return;
        }

        try {
            coreSkillService.saveCoreSkillData();
        } catch (Exception e) {
            log.error("Error while initializing CoreSkill: {}", e.getMessage(), e);
        }
    }
}
