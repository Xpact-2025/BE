package com.itstime.xpact.global.crawler;

import com.itstime.xpact.domain.recruit.dto.DetailRecruitDto;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import com.itstime.xpact.domain.recruit.entity.Recruit;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.domain.recruit.repository.RecruitRepository;
import com.itstime.xpact.global.crawler.dto.RecruitResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
//@Service
@RequiredArgsConstructor
public class CrawlerService {

    private final Crawler crawler;
    private final RecruitRepository recruitRepository;
    private final DetailRecruitRepository detailRecruitRepository;

    public boolean recruitExists() {
        return recruitRepository.count() > 0;
    }

    public boolean detailRecruitExists() {
        return detailRecruitRepository.count() > 0;
    }

    public void saveRecruitData() {
        List<String> recruits = crawler.crawlRecruits();

        List<Recruit> recruitList = recruits.stream()
                .map(recruit -> Recruit.builder()
                        .name(recruit).build())
                .toList();

        recruitRepository.saveAll(recruitList);
        log.info("Saved {} rows of recruits", recruitList.size());
    }

    public void saveDetailRecruitData() {
        List<RecruitResponseDto> recruitResponseDtos = crawler.crawlDetailRecruits();

        List<DetailRecruit> detailRecruitList = recruitResponseDtos.stream()
                .flatMap(recruitResponseDto -> {
                    String recruitName = recruitResponseDto.getGroupName();
                    Recruit recruit = recruitRepository.findByName(recruitName).orElseThrow();

                    return recruitResponseDto.getSubList().stream()
                            .map(sub -> DetailRecruit.builder()
                                    .recruit(recruit)
                                    .name(sub.getSubName())
                                    .build());
                }).toList();

        detailRecruitRepository.saveAll(detailRecruitList);
        log.info("Saved {} rows of detailRecruits", detailRecruitList.size());
    }
}
