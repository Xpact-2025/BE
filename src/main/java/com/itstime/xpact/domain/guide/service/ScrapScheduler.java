package com.itstime.xpact.domain.guide.service;

import com.itstime.xpact.domain.guide.common.ScrapType;
import com.itstime.xpact.domain.guide.dto.ScrapResponseDto;
import com.itstime.xpact.domain.guide.entity.Scrap;
import com.itstime.xpact.domain.guide.repository.ScrapRepository;
import com.itstime.xpact.infra.s3.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScrapScheduler {

    private final ScrapRepository scrapRepository;
    private final FileService fileService;

    @Scheduled(cron = "0 0 * * * *")
    public void removeExpiredScraps() {
        LocalDate now = LocalDate.now();
        scrapRepository.deleteScrapWithEndDate(now);
        log.info("Removed Expired Scraps");
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void updateCrawling() {

        for (ScrapType scrapType : ScrapType.values()) {
            log.info("{} crwaling", scrapType.name());
            List<ScrapResponseDto> crawlingFile = fileService.findCrawlingFile(scrapType);
            if(!crawlingFile.isEmpty()) {
                List<Scrap> competitionList = crawlingFile.stream()
                        .map(ScrapResponseDto::toEntity)
                        .toList();
                scrapRepository.saveAll(competitionList);
                log.info("{} crawling finished", scrapType.name());
            }
        }
    }
}
