package com.itstime.xpact.domain.member;

import com.itstime.xpact.domain.member.repository.SchoolRepository;
import com.itstime.xpact.domain.member.util.SchoolUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
// TODO : ApplicationReadyEvent 적용하기
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class SchoolInitRunner {
//
//    private final SchoolUtil schoolUtil;
//    private final SchoolRepository schoolRepository;
//
//    @EventListener(ApplicationReadyEvent.class)
//    public void onApplicationReady() {
//        try {
//            long schoolCount = schoolRepository.count();
//            if (schoolCount == 0 ) {
//                log.info("School 데이터가 비어있습니다. 파싱 시작...");
//                schoolUtil.syncAllSchools();
//                log.info("School 데이터 저장 완료");
//            } else {
//                log.info("School 데이터가 이미 존재합니다.");
//            }
//        } catch (Exception e) {
//            log.error("School 동기화 중 오류 발생");
//        }
//    }
//}
