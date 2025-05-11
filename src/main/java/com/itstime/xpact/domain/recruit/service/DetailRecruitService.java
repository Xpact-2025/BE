package com.itstime.xpact.domain.recruit.service;

import com.itstime.xpact.domain.recruit.entity.DetailRecruit;
import com.itstime.xpact.domain.recruit.entity.Recruit;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.domain.recruit.repository.RecruitRepository;
import com.itstime.xpact.infra.lambda.LambdaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetailRecruitService {

    private final LambdaUtil lambdaUtil;
    private final RecruitRepository recruitRepository;
    private final DetailRecruitRepository detailRecruitRepository;


    public boolean detailRecruitExists() {
        return detailRecruitRepository.count() > 0;
    }

    public void saveDetailRecruit() {
        List<DetailRecruit> detailRecruits = lambdaUtil.invokeLambda(lambdaUtil.lambdaClient(), "recruitCrawler")
                .stream()
                .flatMap(dtos -> {
                    String recruitName = dtos.getGroupName();
                    Recruit recruit = recruitRepository.findByName(recruitName).orElseThrow();

                    return dtos.getSubList().stream()
                            .map(dto -> DetailRecruit.builder()
                                        .name(dto.getSubName())
                                        .recruit(recruit)
                                        .build());
                }).toList();

        detailRecruitRepository.saveAll(detailRecruits);
        log.info("Saved {} rows of detailRecruits", detailRecruits.size());
    }
}
