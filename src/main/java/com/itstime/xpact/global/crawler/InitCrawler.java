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

    private final CrawlerService crawlerService;

    @Override
    public void run(ApplicationArguments args) {
        if(crawlerService.recruitExists() && crawlerService.detailRecruitExists()) {
            log.info("Data Exists");
            return;
        }

        if(!crawlerService.recruitExists()) crawlerService.saveRecruitData();
        if(!crawlerService.detailRecruitExists()) crawlerService.saveDetailRecruitData();
    }
}
