package com.itstime.xpact.domain.experience.controller;

import com.itstime.xpact.domain.experience.dto.response.DetailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.dto.response.ThumbnailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.service.QueryExperienceService;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.global.response.ErrorResponse;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exp")
@RequiredArgsConstructor
@Tag(name = "Experience Query API Controller", description = "경험 조회 API")
public class QueryExperienceController {

    private final QueryExperienceService queryExperienceService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "모든 경험 조회 시 발생하는 에러 유형", content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "MEMBER_NOT_EXISTS", value = """
                            {
                              "httpStatus": "BAD_REQUEST",
                              "code": "MEMBER001",
                              "message": "존재하지 않는 회원입니다."
                            }""")
                    }
            ))
    })
    @Operation(summary = "사용자의 모든 경험 조회", description = "사용자가 작성한 모든 경험을 조회 (임시저장, 저장 모두 조회), (페이지 처리 X), (상세 조회 X)")
    @GetMapping("")
    public ResponseEntity<RestResponse<List<ThumbnailExperienceReadResponseDto>>> readAllExperience(
            @RequestParam(value = "type", defaultValue = "ALL") List<String> types,
            @RequestParam(value = "order", defaultValue = "latest") String order)
    throws GeneralException {

        return ResponseEntity.ok(RestResponse.ok(queryExperienceService.readAll(types, order.toUpperCase())));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "경험 조회 시 발생하는 에러 유형", content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "EXPERIENCE_NOT_EXISTS", value = """
                            {
                              "httpStatus": "BAD_REQUEST",
                              "code": "EXP001",
                              "message": "해당 경험이 존재하지 않습니다."
                            }"""),
                            @ExampleObject(name = "NOT_YOUR_EXPERIENCE", value = """
                            {
                              "httpStatus": "BAD_REQUEST",
                              "code": "EXP005",
                              "message": "본인의 Experience가 아닙니다."
                            }""")
                    }
            ))
    })
    @Operation(summary = "특정 경험 상세 조회", description = "사용자가 특정 경험을 클릭했을 때, 해당 경험에 대한 모든 정보를 조회")
    @GetMapping("/{experience_id}")
    public ResponseEntity<RestResponse<DetailExperienceReadResponseDto>> readExperience(
            @PathVariable("experience_id") Long experienceId)
    throws GeneralException {

        return ResponseEntity.ok(RestResponse.ok(queryExperienceService.read(experienceId)));
    }

    @Operation(summary = "경험 검색", description = "경험의 제목과 키워드를 통한 검색 기능 제공")
    @GetMapping("/search")
    public ResponseEntity<RestResponse<List<ThumbnailExperienceReadResponseDto>>> queryExperience(
            @RequestParam("query") @Nullable String query) {

        return ResponseEntity.ok(RestResponse.ok(queryExperienceService.query(query)));
    }
}
