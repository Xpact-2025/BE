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

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScrapScheduler {

    private final ScrapRepository scrapRepository;
    private final FileService fileService;

    @Scheduled(cron = "0 0 3 * * *")
    public void updateCrawling() {
        for (ScrapType scrapType : List.of(ScrapType.INTERN, ScrapType.EDUCATION, ScrapType.ACTIVITY, ScrapType.COMPETITION)) {
            log.info("{} crwaling", scrapType.name());
            List<ScrapResponseDto> crawlingFiles = fileService.findCrawlingFile(scrapType);
            if(!crawlingFiles.isEmpty()) {
                List<Scrap> competitionList = crawlingFiles.stream()
                        .map(scrapResponseDto -> scrapResponseDto.toEntity(scrapType))
                        .toList();
                int count = scrapRepository.saveAllWithIgnore(competitionList);
                log.info("{} rows saved", count);
                log.info("{} crawling finished", scrapType.name());
            }
        }
    }
}
