package com.itstime.xpact.domain.dashboard.controller;

import com.itstime.xpact.domain.dashboard.dto.response.RatioResponseDto;
import com.itstime.xpact.domain.dashboard.service.DashboardService;
import com.itstime.xpact.domain.dashboard.service.ScoreResultStore;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.response.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ScoreResultStore scoreResultStore;

    // 점수 계산 환산 확인을 위한 Test Controller
    // TODO : 추후에 수정 필요
    @PostMapping
    public ResponseEntity<?> getScore(
            @RequestHeader("Authorization") String token) throws CustomException {
        dashboardService.coreSkillMap();
        return ResponseEntity.accepted()
                .body("Request Accepted.");
    }

    @PostMapping("/{memberId}")
    public ResponseEntity<?> getScoreResult(
            @RequestHeader("Authorization") String token,
            @PathVariable Long memberId) throws CustomException {

        String result = scoreResultStore.get(memberId);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.PROCESSING).body("Request Processing");
        }
        return ResponseEntity.ok(
                RestResponse.ok(result)
        );
    }

    @GetMapping("/ratio")
    public ResponseEntity<RestResponse<RatioResponseDto>> getRatio() {
        return ResponseEntity.ok(RestResponse.ok(dashboardService.detailRecruitRatio()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RestResponse<?>> refresh() {
        dashboardService.refreshData();
        return ResponseEntity.ok(RestResponse.ok());
    }
}
