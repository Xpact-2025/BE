package com.itstime.xpact.global.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
@RequiredArgsConstructor
public class InitCrawler implements ApplicationRunner {

    private final CrawlerService crawlerService;

    @Override
    public void run(ApplicationArguments args) {
        if(!checkRecruitData()) crawlerService.saveRecruitData();
        else log.info("Recruit Column has already been initialized");

        if(!checkDetailRecruitData()) crawlerService.saveDetailRecruitData();
        else log.info("DetailRecruit Column has already been initialized");

    }

    private boolean checkRecruitData() {
        return crawlerService.recruitExists();
    }

    private boolean checkDetailRecruitData() {
        return crawlerService.detailRecruitExists();
    }
}
