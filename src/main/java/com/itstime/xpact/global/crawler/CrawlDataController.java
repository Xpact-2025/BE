package com.itstime.xpact.global.crawler;

import com.itstime.xpact.domain.recruit.entity.Recruit;
import com.itstime.xpact.domain.recruit.repository.DetailRecruitRepository;
import com.itstime.xpact.domain.recruit.repository.RecruitRepository;
import com.itstime.xpact.global.response.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@RestController
@RequiredArgsConstructor
public class CrawlDataController {

    private final DetailRecruitRepository detailRecruitRepository;
    private final RecruitRepository recruitRepository;

    @GetMapping("/crawler/recruit")
    public ResponseEntity<RestResponse<List<String>>> getRecruits() {
        List<String> recruits = recruitRepository.findAll()
                .stream()
                .map(Recruit::getName).toList();
        return ResponseEntity.ok(RestResponse.ok(recruits));
    }

    @GetMapping("/crawler/detail-recruit")
    public ResponseEntity<RestResponse<List<String>>> getDetailRecruits() {
        List<String> detailRecruits = detailRecruitRepository.findAllWithRecruit()
                .stream()
                .map(detailRecruit ->
                        detailRecruit.getName() + "(" + detailRecruit.getRecruit().getName() + ")")
                .toList();

        return ResponseEntity.ok(RestResponse.ok(detailRecruits));
    }
}
