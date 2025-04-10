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

    private final WebClient.Builder webClientBuilder;
    private final SchoolCustomRepositoryImpl schoolCustomRepository;

    @Value("${external.api.school.service-key}")
    private String serviceKey;

    private static final String BASE_URL = "https://openapi.academyinfo.go.kr";
    private static final String SCHOOL_OPEN_API_URL = "/openapi/service/rest/SchoolMajorInfoService/getSchoolMajorInfo";

    public void syncAllSchools() throws CustomException {
        int year = 2024;
        int page = 1;
        int numOfRows = 100;
        int totalCount;

        do {
            try {
                SchoolInfoResponseDto responseDto = webClientBuilder
                        .baseUrl(BASE_URL)
                        .build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path(SCHOOL_OPEN_API_URL)
                                .queryParam("serviceKey", serviceKey)
                                .queryParam("svyYr", year)
                                .queryParam("pageNo", page)
                                .queryParam("numOfRows", numOfRows)
                                .build())
                        .retrieve()
                        .bodyToMono(SchoolInfoResponseDto.class)
                        .block();

                if (responseDto == null || responseDto.getBody().getItems() == null) {
                    log.warn("항목이 존재하지 않습니다.");
                    break;
                }

                totalCount = responseDto.getBody().getTotalCount();
                log.info("{} 개 중 page {} 수신 완료", totalCount, page);

                responseDto.getBody().getItems().getItemList().forEach(
                        item -> {
                            String schoolName = item.getSchoolName();
                            schoolCustomRepository.saveIfNotExist(schoolName);
                        });
            } catch (Exception e) {
                log.error("검색 도중 오류 발생 ... ");
                throw CustomException.of(ErrorCode.PARSING_ERROR);
            }
        } while ((page - 1) * numOfRows < totalCount);
    }
}
