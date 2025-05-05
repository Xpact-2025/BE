package com.itstime.xpact.domain.recruit.controller;

import com.itstime.xpact.domain.recruit.dto.request.DesiredRecruitRequestDto;
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

    @Operation(summary = "산업 별 검색 자동완성 API", description = """
    산업 부문 중 검색과 일치하는 부문에 대하여 반환합니다.<br>
    Header에 accessToken 값을 넣어주세요.
    """)
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

    @Operation(summary = "회원 희망 직무 저장 API", description = """
    회원이 여태 선택한 것을 바탕으로 희망 직무 정보를 저장합니다.<br>
    Header에 accessToken 값을 넣어주세요.
    """)
    @PatchMapping
    public ResponseEntity<RestResponse<?>> updateDesiredRecruit(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "희망 직무 요청 DTO", required = true)
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
