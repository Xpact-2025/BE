package com.itstime.xpact.domain.recruit.controller;

import com.itstime.xpact.domain.recruit.service.RecruitService;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "산업 별 전체 조회 API", description = """
    직무 전체 산업 부문을 반환합니다.<br>
    Header에 accessToken 값을 넣어주세요.
    """)
    @GetMapping("/name")
    public ResponseEntity<RestResponse<?>> findAllName(
            @RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(
                RestResponse.ok(
                        recruitService.readAllRecruit()
                )
        );
    }

    @Operation(summary = "상세 직무 전체 조회 API", description = """
    희망 산업을 선택 후, 그 산업에 해당되는 직무들을 반환합니다.<br>
    Header에 accessToken 값을 넣어주세요.
    """)
    @GetMapping("/{name}/detail")
    public ResponseEntity<RestResponse<?>> findAllDetailRecruit(
            @RequestHeader("Authorization") String token,
            @PathVariable String name
    ) {
        return ResponseEntity.ok(
                RestResponse.ok(recruitService.readAllDetailRecruits(name))
        );
    }

    @Operation(summary = "상세 직무 검색 자동완성 API", description = """
    희망 산업을 선택 후, 상세 직무 검색 시 검색어와 일치하는 상세직무들을 반환합니다.<br>
    Header에 accessToken 값을 넣어주세요.
    """)
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
}
