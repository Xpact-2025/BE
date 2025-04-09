package com.itstime.xpact.global.crawler;

import com.itstime.xpact.domain.recruit.dto.DetailRecruitDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Crawler {

    // 직무 데이터
    public List<String> getRecruits() {
        return List.of("A", "B", "C");
    }

    // 상세 직무 데이터 Map<String, String> => <직무, 상세직무>
    public List<DetailRecruitDto> getDetailRecruits() {
        return List.of(DetailRecruitDto.of("A", "AA"),
                DetailRecruitDto.of("A", "AB"),
                DetailRecruitDto.of("B", "BA"),
                DetailRecruitDto.of("B", "BC"),
                DetailRecruitDto.of("C", "CC"),
                DetailRecruitDto.of("C", "CD")
                );
    }
}
