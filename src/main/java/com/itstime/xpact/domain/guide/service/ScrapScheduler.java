package com.itstime.xpact.domain.guide.service;

import com.itstime.xpact.domain.guide.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScrapScheduler {

    private final ScrapRepository scrapRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void removeExpiredScraps() {
        LocalDate now = LocalDate.now();
        scrapRepository.deleteScrapWithEndDate(now);
        log.info("Removed Expired Scraps");
    }
}
