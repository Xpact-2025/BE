package com.itstime.xpact.domain.recruit.controller;

import com.itstime.xpact.domain.recruit.dto.request.DesiredRecruitRequestDto;
import com.itstime.xpact.domain.recruit.service.RecruitService;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruits")
@RequiredArgsConstructor
@Tag(name = "희망 직무 저장 API Controller", description = "희망 직무 저장을 위한 API 입니다.")
public class RecruitController {

    private final RecruitService recruitService;

    @GetMapping("/name")
    public ResponseEntity<RestResponse<?>> findAllName(
            @RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(
                RestResponse.ok(
                        recruitService.readAllRecruit()
                )
        );
    }

    @GetMapping("/name/search")
    public ResponseEntity<RestResponse<?>> searchRecruit(
            @RequestHeader("Authorization") String token,
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(
                RestResponse.ok(
                        recruitService.autocompleteName(keyword)
                )
        );
    }

    @GetMapping("/{name}/detail")
    public ResponseEntity<RestResponse<?>> findAllDetailRecruit(
            @RequestHeader("Authorization") String token,
            @PathVariable String name
    ) {
        return ResponseEntity.ok(
                RestResponse.ok(recruitService.readAllDetailRecruits(name))
        );
    }

    @GetMapping("/{name}/detail/search")
    public ResponseEntity<RestResponse<?>> searchDetailRecruit(
            @RequestHeader("Authorization") String token,
            @PathVariable String name,
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(
                RestResponse.ok(
                        recruitService.autocompleteDetail(name, keyword)
                )
        );
    }

    @PatchMapping
    public ResponseEntity<RestResponse<?>> updateDesiredRecruit(
            @RequestHeader("Authorization") String token,
            @RequestBody DesiredRecruitRequestDto requestDto
    ) {
        return ResponseEntity.ok(
                RestResponse.ok(
                        recruitService.updateDesiredRecruit(requestDto)
                )
        );
    }
}
