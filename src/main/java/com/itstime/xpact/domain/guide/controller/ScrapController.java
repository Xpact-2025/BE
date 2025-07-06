package com.itstime.xpact.domain.guide.controller;

import com.itstime.xpact.domain.guide.dto.response.ScrapThumbnailResponseDto;
import com.itstime.xpact.domain.guide.service.ScrapService;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scrap")
@Tag(name = "스크랩 API Controller")
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrapService;

    @PostMapping("/{scrap_id}")
    public ResponseEntity<RestResponse<?>> onScrap(@PathVariable(name = "scrap_id") Long scrapId) {
        scrapService.onScrap(scrapId);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @DeleteMapping("/{scrap_id}")
    public ResponseEntity<RestResponse<?>> offScrap(@PathVariable(name = "scrap_id") Long scrapId) {
        scrapService.offScrap(scrapId);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @GetMapping("/activities")
    public ResponseEntity<RestResponse<List<ScrapThumbnailResponseDto>>> getActivities() {
        return ResponseEntity.ok(RestResponse.ok(scrapService.getActivitites()));
    }
}
