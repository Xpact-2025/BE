package com.itstime.xpact.global.crawler;

import com.itstime.xpact.domain.recruit.dto.DetailRecruitDto;
import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import com.itstime.xpact.domain.recruit.entity.Recruit;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.domain.recruit.repository.RecruitRepository;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingService {

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

        List<String> recruits = crawler.getRecruits();

        List<Recruit> recruitList = recruits.stream()
                .map(recruit -> Recruit.builder()
                        .name(recruit).build())
                .toList();

        recruitRepository.saveAll(recruitList);
        log.info("Save recruits");
    }

    public void saveDetailRecruitData() {
        List<DetailRecruitDto> detailRecruits = crawler.getDetailRecruits();

        List<DetailRecruit> detailRecruitList = detailRecruits.stream()
                .map(detailRecruitDto -> {
                    String recruitName = detailRecruitDto.getRecruitName();
                    Recruit recruit = recruitRepository.findByName(recruitName)
                            .orElseThrow();

                    return DetailRecruit.builder()
                            .recruit(recruit)
                            .name(detailRecruitDto.getDetailRecruitName())
                            .build();
                }).toList();

        detailRecruitRepository.saveAll(detailRecruitList);
        log.info("Save detail recruits");
    }
}
