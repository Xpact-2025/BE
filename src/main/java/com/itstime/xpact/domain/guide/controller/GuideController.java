package com.itstime.xpact.domain.guide.controller;

import com.itstime.xpact.domain.guide.service.GuideService;
import com.itstime.xpact.global.response.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guide")
public class GuideController {

    private final GuideService guideService;

    @PostMapping("/weakness")
    public ResponseEntity<RestResponse<?>> analysis() {
        guideService.saveWeakness();
        return ResponseEntity.ok(
                RestResponse.ok()
        );
    }

    @GetMapping("/weakness")
    public ResponseEntity<RestResponse<?>> getWeakness() {
        return ResponseEntity.ok(
                RestResponse.ok(
                        guideService.getAnalysis()
                )
        );
    }
}
