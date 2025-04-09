package com.itstime.xpact.global.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitCrawler implements ApplicationRunner {

    private final CrawlingService crawlingService;

    @Override
    public void run(ApplicationArguments args) {
        if(!crawlingService.dataExists()) {
            log.info("Data Exists");
            return;
        }

        crawlingService.saveRecruitData();
        crawlingService.saveDetailRecruitData();
    }
}
