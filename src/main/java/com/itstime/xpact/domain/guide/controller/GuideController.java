package com.itstime.xpact.domain.guide.controller;

import com.itstime.xpact.domain.guide.dto.response.ScrapDetailResponseDto;
import com.itstime.xpact.domain.guide.service.GuideService;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "성장 가이드 API Controller")
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

    @Operation(summary = "약점 기반 AI 추천 활동", description = """
            약점 별 공고를 추천하는 API입니다.<br>
            조회하고 싶은 약점이 몇번째인지에 대하여 입력해주세요.<br>
            0은 전체, 1은 첫번째 약점, 2는 두번째 약점, 3은 세번째 약점으로 조회됩니다.
            """)
    @GetMapping("/activities")
    public ResponseEntity<RestResponse<?>> getActivities(
            @Parameter @RequestParam(name = "weaknessOrder") int weaknessOrder,
            @ParameterObject @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(RestResponse.ok(guideService.getActivities(weaknessOrder, pageable)));
    }

    @GetMapping("/activities/{activity_id}")
    public ResponseEntity<RestResponse<ScrapDetailResponseDto>> getActivities(@PathVariable(name = "activity_id") Long scrapId) {
        return ResponseEntity.ok(RestResponse.ok(guideService.getActivity(scrapId)));
    }
}
