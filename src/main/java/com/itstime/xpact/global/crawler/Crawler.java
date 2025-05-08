package com.itstime.xpact.global.crawler;

import com.itstime.xpact.global.crawler.dto.RecruitResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
@RequiredArgsConstructor
public class Crawler {

    private final JobKoreaCrawler jobKoreaCrawler;

    public List<String> crawlRecruits() {
        return jobKoreaCrawler.crawlRecruits().stream()
                .map(RecruitResponseDto::getGroupName)
                .toList();
    }

    public List<RecruitResponseDto> crawlDetailRecruits() {
        return jobKoreaCrawler.crawlRecruits();
    }
}
