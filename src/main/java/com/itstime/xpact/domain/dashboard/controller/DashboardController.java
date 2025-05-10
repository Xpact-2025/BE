package com.itstime.xpact.domain.dashboard.controller;

import com.itstime.xpact.domain.dashboard.service.DashboardService;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.response.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    // 점수 계산 환산 확인을 위한 Test Controller
    // TODO : 추후에 수정 필요
    @GetMapping
    public ResponseEntity<RestResponse<?>> getScore(
            @RequestHeader("Authorization") String token) throws CustomException {
        return ResponseEntity.ok(
                RestResponse.ok(dashboardService.coreSkillMap())
        );
    }
}
