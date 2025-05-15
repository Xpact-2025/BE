package com.itstime.xpact.domain.dashboard.controller;

import com.itstime.xpact.domain.dashboard.dto.response.HistoryResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.MapResponseDto;
import com.itstime.xpact.domain.dashboard.dto.response.RatioResponseDto;
import com.itstime.xpact.domain.dashboard.service.DashboardService;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
@Tag(name = "대시보드 API Controller", description = "대시보드 페이지 API")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "핵심스킬맵 요청 API", description = """
            핵심스킬맵을 추출하기 위한 요청을 보내는 API입니다.<br>
            요청이 성공되면 Reqeust Accepted 응답이 반환되며,<br>
            해당 반환의 memberId로 GET 요청을 보내면 응답을 확인할 수 있습니다.<br>
            핵심스킬맵 결과를 새로 확인하고 싶을 때도 사용이 가능합니다.
            """)
    @PostMapping("/skills")
    public ResponseEntity<?> evaluateScoreAsync(
            @RequestHeader("Authorization") String token) throws CustomException {

        Long memberId = dashboardService.evaluateAsync();
        return ResponseEntity.accepted().body("Request Accepted for memberId: " + memberId);
    }

    @Operation(summary = "핵심스킬맵 추출 API", description = """
            핵심스킬맵 반환 결과에 대한 API입니다.<br>
            memberId를 path에 넣으면, 해당 회원의 핵심스킬맵 결과가 나옵니다.<br>
            만약 아직 결과가 요청을 받는 중이라면, Processing의 응답이 나오니,<br>
            잠시 후에 다시 요청해주세요.
            """)
    @ApiResponse(responseCode = "200", description = "응답 반환 성공",
    content = @Content(schema = @Schema(implementation = MapResponseDto.class)))
    @GetMapping("/skills/{memberId}")
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

    @Operation(summary = "직무비율 반환 API", description = """
            사용자의 경험 기반 직무비율 산정 결과를 나타내는 API 입니다.
            """)
    @ApiResponse(responseCode = "200", description = "비율 산정 성공",
    content = @Content(schema = @Schema(implementation = RatioResponseDto.class)))
    @GetMapping("/ratio")
    public ResponseEntity<RestResponse<RatioResponseDto>> getRatio() {
        return ResponseEntity.ok(RestResponse.ok(dashboardService.detailRecruitRatio()));
    }

    @Operation(summary = "사용자 경험 반영 API", description = """
            대시보드의 결과에 반영할 사용자 기반 경험 데이터를 갱신하는 API입니다.<br>
            데이터 갱신을 통하여 사용자 경험의 최신 정보를 반영하여,<br>
            대시보드에 반영합니다.<br>
            병목현상으로 인한 오류가 있어 추후 수정이 필요할 것 같습니다.
            """)
    @PostMapping("/refresh")
    public ResponseEntity<RestResponse<?>> refresh() {
        dashboardService.refreshData();
        return ResponseEntity.ok(RestResponse.ok());
    }

    @Operation(summary = " 경험 히스토리 반환 API", description = """
            사용자의 경험 생성 히스토리를 조회합니다.
            """)
    @ApiResponse(responseCode = "200", description = "응답 반환 성공",
            content = @Content(schema = @Schema(implementation = RatioResponseDto.class)))
    @GetMapping("/history")
    public ResponseEntity<RestResponse<HistoryResponseDto>> getHistory(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        HistoryResponseDto dto = dashboardService.getCountPerDay(year, month);
        return ResponseEntity.ok(RestResponse.ok(dto));
    }
}
