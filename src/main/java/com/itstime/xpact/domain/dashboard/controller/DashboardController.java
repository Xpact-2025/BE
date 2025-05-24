package com.itstime.xpact.domain.dashboard.controller;

import com.itstime.xpact.domain.dashboard.dto.response.*;
import com.itstime.xpact.domain.dashboard.service.DashboardService;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.response.ErrorResponse;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
@Tag(name = "대시보드 API Controller", description = "대시보드 페이지 API")
public class DashboardController {

    private final DashboardService dashboardService;


    @Operation(summary = "핵심스킬맵 요청 API", description = """
            핵심스킬맵 대시보드 반환을 위한 API입니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "저장된 경험이 없을 때의 에러 유형", content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "EXPERIENCES_NOT_ENOUGH", value = """
                        {
                              "httpStatus": "BAD_REQUEST",
                              "code": "SME002",
                              "message": "입력된 경험이 충분하지 않습니다."
                        }""")
                    }
            ))
    })
    @ApiResponse(responseCode = "200", description = "핵심스킬맵 요청 성공",
            content = @Content(schema = @Schema(implementation = MapResponseDto.class)))
    @PostMapping("/skills")
    public DeferredResult<ResponseEntity<?>> evaluateScore(
            @RequestHeader("Authorization") String token) throws GeneralException {

        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(20_000L);

        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(
                    ErrorResponse.toResponseEntity(ErrorCode.REQUEST_TIMEOUT)
            );
        });

        dashboardService.evaluateScore()
                .thenAccept(result -> {
                    deferredResult.setResult(ResponseEntity.ok(
                            RestResponse.ok(result)
                    ));
                })
                .exceptionally(e -> {
                    deferredResult.setErrorResult(
                            ErrorResponse.toResponseEntity(ErrorCode.FAILED_GET_RESULT)
                    );
                    return null;
                });
        return deferredResult;
    }

    @Operation(summary = "직무비율 반환 API", description = """
            사용자의 경험 기반 직무비율 산정 결과를 나타내는 API 입니다.
            """)
    @ApiResponse(responseCode = "200", description = "비율 산정 성공",
    content = @Content(schema = @Schema(implementation = RatioResponseDto.class)))
    @GetMapping("/ratio")
    public ResponseEntity<RestResponse<RatioResponseDto>> getRatio() {
        return ResponseEntity.ok(RestResponse.ok(dashboardService.getRecruitCategoryRatio()));
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
    public ResponseEntity<RestResponse<HistoryOldResponseDto>> getOldHistory(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        HistoryOldResponseDto dto = dashboardService.getOldExperienceHistory(year, month);
        return ResponseEntity.ok(RestResponse.ok(dto));
    }

    @Operation(summary = "경험 히스토리 반환 API (new)", description = """
            사용자의 경험 생성 히스토리를 3개월 단위로 조회합니다.
            """)
    @GetMapping("/history-new")
    public ResponseEntity<RestResponse<HistoryResponseDto>> getHistory(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        HistoryResponseDto dto = dashboardService.getExperienceHistory(year, month);
        return ResponseEntity.ok(RestResponse.ok(dto));
    }


    @Operation(summary = "타임라인 조회 API",
    description = """
            경험에 대한 타임라인 조회를 위한 API입니다.<br>
            조회할 기간의 시작날짜와 종료날짜를 입력해주세요.
            """)
    @ApiResponse(responseCode = "200", description = "타임라인 조회 성공",
    content = @Content(schema = @Schema(implementation = TimelineResponseDto.class)))
    @GetMapping("/timeline")
    public ResponseEntity<RestResponse<?>> getTimeline(
            @Parameter(
                    description = "조회 기간 시작 날짜 (yyyy-MM-dd)",
                    required = true,
                    example = "2025-03-01"
            )
            @RequestParam("startLine") LocalDate startLine,
            @Parameter(
                    description = "조회 기간 종료 날짜 (yyyy-MM-dd)",
                    required = true,
                    example = "2025-07-31"
            )
            @RequestParam("endLine") LocalDate endLine
            ) {
        return ResponseEntity.ok(
                RestResponse.ok(dashboardService.getExperienceTimeline(startLine, endLine)
                )
        );
    }

    @Operation(summary = "핵심스킬맵 강점 역량 피드백 요청 API", description = """
            사용자의 경험 기반으로 핵심 역량 중 강점에 대한 피드백을 제시합니다.<br>
            expAnalysis는 경험 기반 분석으로,<br>
            recommend는 커리어 연결로 매핑하시면 됩니다.
            """)
    @ApiResponse(responseCode = "200", description = "역량 피드백 요청 성공",
            content = @Content(schema = @Schema(implementation = FeedbackResponseDto.class)))
    @PostMapping("/skills/feedback/strength")
    public DeferredResult<ResponseEntity<?>> getFeedbackStrength() {

        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(20_000L);

        dashboardService.getStrengthFeedback()
                .thenAccept(result -> {
                    deferredResult.setResult(ResponseEntity.ok(
                            RestResponse.ok(result)
                    ));
                })
                .exceptionally(e -> {
                    deferredResult.setErrorResult(
                            ErrorResponse.toResponseEntity(ErrorCode.FAILED_GET_RESULT)
                    );
                    return null;
                });
        return deferredResult;
    }

    @Operation(summary = "핵심스킬맵 약점 역량 피드백 요청 API", description = """
            사용자의 경험 기반으로 핵심 역량 중 약점에 대한 피드백을 제시합니다.<br>
            expAnalysis는 경험 기반 분석으로,<br>
            recommend는 추천 활동으로 매핑하시면 됩니다.
            """)
    @ApiResponse(responseCode = "200", description = "역량 피드백 요청 성공",
            content = @Content(schema = @Schema(implementation = FeedbackResponseDto.class)))
    @PostMapping("/skills/feedback/weakness")
    public DeferredResult<ResponseEntity<?>> getFeedbackWeakness() {

        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(20_000L);

        dashboardService.getWeaknessFeedback()
                .thenAccept(result -> {
                    deferredResult.setResult(ResponseEntity.ok(
                            RestResponse.ok(result)
                    ));
                })
                .exceptionally(e -> {
                    deferredResult.setErrorResult(
                            ErrorResponse.toResponseEntity(ErrorCode.FAILED_GET_RESULT)
                    );
                    return null;
                });
        return deferredResult;
    }
}
