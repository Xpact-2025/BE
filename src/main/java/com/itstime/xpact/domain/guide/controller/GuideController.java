package com.itstime.xpact.domain.guide.controller;

import com.itstime.xpact.domain.guide.service.GuideService;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "가이드 API Controller")
@RequestMapping("/api/guide")
public class GuideController {

    private final GuideService guideService;

    @Operation(summary = "약점에 대한 분석 조회 API", description = """
            약점에 대한 분석을 조회하고자 할 때 사용하는 API입니다.<br>
            만약 대시보드에서 핵심스킬맵 산정이 안 되어있다면,<br>
            이를 우선으로 실행 뒤에 요청해주세요.
            """)
    @GetMapping("/weakness")
    public ResponseEntity<RestResponse<?>> getWeakness() {
        return ResponseEntity.ok(
                RestResponse.ok(
                        guideService.getAnalysis()
                )
        );
    }
}
