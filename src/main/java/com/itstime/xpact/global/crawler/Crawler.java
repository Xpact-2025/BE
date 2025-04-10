package com.itstime.xpact.global.crawler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.domain.recruit.dto.DetailRecruitDto;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Crawler {

    private static final String RECRUIT_JSON = "src/main/resources/static/recruit.json";
    private static final String DETAIL_RECRUIT_JSON = "src/main/resources/static/detailRecruit.json";
    private final ObjectMapper objectMapper;

    // 직무 데이터
    public List<String> getRecruits() {
        List<String> recruits = new ArrayList<>();
        try {
            JsonNode jsonNodes = objectMapper.readTree(new File(RECRUIT_JSON));
            for (JsonNode jsonNode : jsonNodes) {
                recruits.add(jsonNode.get("recruit").asText());
            }
        } catch (IOException e) {
            throw CustomException.of(ErrorCode.TEST);
        }

        return recruits;
    }

    public List<DetailRecruitDto> getDetailRecruits() {
        List<DetailRecruitDto> detailRecruits = new ArrayList<>();
        try {
            JsonNode jsonNodes = objectMapper.readTree(new File(DETAIL_RECRUIT_JSON));
            for (JsonNode jsonNode : jsonNodes) {
                DetailRecruitDto detailRecruitDto = objectMapper.treeToValue(jsonNode, DetailRecruitDto.class);
                detailRecruits.add(detailRecruitDto);
            }
        } catch (IOException e) {
            throw CustomException.of(ErrorCode.TEST);
        }

        return detailRecruits;
    }
}
