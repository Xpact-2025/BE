package com.itstime.xpact.domain.dashboard.controller;

import com.itstime.xpact.domain.dashboard.dto.response.MapResponseDto;
import com.itstime.xpact.domain.dashboard.service.DashboardService;
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

    @PostMapping
    public ResponseEntity<?> evaluateScoreAsync(
            @RequestHeader("Authorization") String token) throws CustomException {

        Long memberId = dashboardService.evaluateAsync();
        return ResponseEntity.accepted().body("Request Accepted for memberId: " + memberId);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<?> getScoreResult(
            @RequestHeader("Authorization") String token,
            @PathVariable Long memberId) throws CustomException {

        MapResponseDto response = dashboardService.getEvaluationResult(memberId)
                .orElse(null);

        if (response == null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Processing");
        }

        return ResponseEntity.ok(RestResponse.ok(response));
    }
}
