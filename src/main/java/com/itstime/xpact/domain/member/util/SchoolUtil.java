package com.itstime.xpact.domain.member.util;

import com.itstime.xpact.domain.member.dto.response.SchoolInfoResponseDto;
import com.itstime.xpact.domain.member.repository.SchoolCustomRepositoryImpl;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class SchoolUtil {

    private final WebClient schoolWebClient;
    private final SchoolCustomRepositoryImpl schoolCustomRepository;

    @Value("${external.api.school.service-key}")
    private String serviceKey;

    private static final String SCHOOL_OPEN_API_URL = "/openapi/service/rest/SchoolMajorInfoService/getSchoolMajorInfo";


    public void syncAllSchools() throws CustomException {
        int page = 1;
        int numOfRows = 100;
        int totalCount;

        do {
            try {

                SchoolInfoResponseDto responseDto = fetchSchoolInfo(page, numOfRows);

                if (responseDto == null || responseDto.getBody() == null ||
                        responseDto.getBody().getItems() == null ||
                        responseDto.getBody().getItems().getItemList() == null) {
                    log.warn("학교 동기화: 응답이 비어있거나 항목이 없습니다. page={}", page);
                    break;
                }

                totalCount = responseDto.getBody().getTotalCount();
                log.info("학교 동기화: 총 {}개 중 page {} 완료", totalCount, page);

                responseDto.getBody().getItems().getItemList()
                        .forEach(item -> schoolCustomRepository.saveIfNotExist(item.getSchoolName(), item.getMajor()));

                page++;
            } catch (Exception e) {
                log.error("학교 동기화 실패: {}", e.getMessage(), e);
                throw new CustomException(ErrorCode.PARSING_ERROR);
            }
        } while ((page - 1) * numOfRows < totalCount);
    }

    private SchoolInfoResponseDto fetchSchoolInfo(int page, int numOfRows) {
        return schoolWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SCHOOL_OPEN_API_URL)
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("svyYr", 2024)
                        .queryParam("pageNo", page)
                        .queryParam("numOfRows", numOfRows)
                        .build())
                .retrieve()
                .bodyToMono(SchoolInfoResponseDto.class)
                .block();
    }
}
