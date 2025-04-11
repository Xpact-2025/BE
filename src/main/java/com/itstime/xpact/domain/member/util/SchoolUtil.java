package com.itstime.xpact.domain.member.util;

import com.itstime.xpact.domain.member.dto.response.SchoolInfoResponseDto;
import com.itstime.xpact.domain.member.repository.SchoolCustomRepositoryImpl;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class SchoolUtil {

    private final SchoolCustomRepositoryImpl schoolCustomRepository;

    @Qualifier("xmlWebClient")
    private final WebClient xmlWebClient;

    @Value("${external.api.school.service-key}")
    private String serviceKey;

    private static final String BASE_URL = "http://openapi.academyinfo.go.kr";
    private static final String SCHOOL_OPEN_API_URL = "/openapi/service/rest/SchoolMajorInfoService/getSchoolMajorInfo";


    public void syncAllSchools() throws CustomException {
        int page = 1;
        int numOfRows = 100;
        int totalCount;

        do {
            try {

                SchoolInfoResponseDto responseDto = xmlWebClient
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path(SCHOOL_OPEN_API_URL)
                                .queryParam("serviceKey", serviceKey)
                                .queryParam("svyYr", 2024)
                                .queryParam("pageNo", 1)
                                .queryParam("numOfRows", numOfRows)
                                .build())
                        .retrieve()
                        .bodyToMono(SchoolInfoResponseDto.class)
                        .block();

                if (responseDto == null ||
                        responseDto.getBody() == null ||
                        responseDto.getBody().getItems() == null ||
                        responseDto.getBody().getItems().getItemList() == null) {
                    log.warn("응답이 비어있거나 항목이 없습니다.");
                    break;
                }

                totalCount = responseDto.getBody().getTotalCount();
                log.info("{} 개 중 page {} 수신 완료", totalCount, page);

                responseDto.getBody().getItems().getItemList().forEach(item -> {
                    String schoolName = item.getSchoolName();
                    String majorName = item.getMajor();
                    schoolCustomRepository.saveIfNotExist(schoolName, majorName);
                });

                page++; // 페이지 증가 필수
            } catch (Exception e) {
                log.error("학교 정보 동기화 중 오류 발생: {}", e.getMessage(), e);
                throw CustomException.of(ErrorCode.PARSING_ERROR);
            }
        } while ((page - 1) * numOfRows < totalCount);
    }
}
