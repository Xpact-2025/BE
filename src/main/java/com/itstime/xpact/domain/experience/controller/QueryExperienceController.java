package com.itstime.xpact.domain.experience.controller;

import com.itstime.xpact.domain.experience.dto.DetailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.dto.ThumbnailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.service.QueryExperienceService;
import com.itstime.xpact.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/exp")
@RequiredArgsConstructor
@Tag(name = "Experience Read API Controller", description = "경험 조회 API (저장, 임시저장과 무관)")
public class QueryExperienceController {

    private final QueryExperienceService queryExperienceService;

    @Operation(summary = "사용자의 모든 경험 조회", description = "사용자가 작성한 모든 경험을 조회합니다. (임시저장, 저장 모두 조회), (페이지 처리 X), (상세 조회 X)")
    @GetMapping("/")
    public ApiResponse<List<ThumbnailExperienceReadResponseDto>> readAllExperience() {
        return ApiResponse.onSuccess(queryExperienceService.readAll());
    }

    @Operation(summary = "특정 경험 상세 조회", description = "사용자가 특정 경험을 클릭했을 때, 해당 경험에 대한 모든 정보를 가져옵니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXP001", description = "존재하지 않는 경험")
    })
    @GetMapping("/{experience_id}")
    public ApiResponse<DetailExperienceReadResponseDto> readExperience(@PathVariable("experience_id") Long experienceId) {
        return ApiResponse.onSuccess(queryExperienceService.read(experienceId));
    }
}
