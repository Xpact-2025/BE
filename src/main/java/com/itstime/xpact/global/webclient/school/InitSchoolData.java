package com.itstime.xpact.global.webclient.school;

import com.itstime.xpact.domain.member.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class InitSchoolData {

    private final SchoolApiClient schoolApiClient;
    private final SchoolRepository schoolRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            long schoolCount = schoolRepository.count();
            if (schoolCount == 0 ) {
                log.info("School 데이터가 비어있습니다. 파싱 시작...");
                schoolApiClient.syncAllSchools();
                log.info("School 데이터 저장 완료");
            } else {
                log.info("School 데이터가 이미 존재합니다.");
            }
        } catch (Exception e) {
            log.error("School 동기화 중 오류 발생");
        }
    }
}
