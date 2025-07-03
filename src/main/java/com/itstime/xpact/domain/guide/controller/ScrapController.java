package com.itstime.xpact.domain.guide.controller;

import com.itstime.xpact.domain.guide.service.ScrapService;
import com.itstime.xpact.global.response.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scrap")
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
}
