package com.itstime.xpact.domain.experience.controller;

import com.itstime.xpact.domain.experience.dto.DetailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.dto.ThumbnailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.service.ExperienceService;
import com.itstime.xpact.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/exp")
@RequiredArgsConstructor
@Tag(name = "Experience API Controller", description = "경험 관련 API")
public class ExperienceController {

    private final ExperienceService experienceService;

    @Operation(summary = "경험 생성", description = "주어진 데이터로 경험 생성 (사용자 정보는 인가처리된 사용자 정보 사용)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CATEGORY001", description = "잘못된 카테고리"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXP002", description = "잘못된 유형")
    })
    @PostMapping("/")
    public ApiResponse<?> createExperience(@RequestBody ExperienceCreateRequestDto createRequestDto) {
        experienceService.create(createRequestDto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "사용자의 모든 경험 조회", description = "사용자가 작성한 모든 경험을 조회합니다. (임시저장, 저장 모두 조회), (페이지 처리 X), (상세 조회 X)")
    @GetMapping("/")
    public ApiResponse<List<ThumbnailExperienceReadResponseDto>> readAllExperience() {
        return ApiResponse.onSuccess(experienceService.readAll());
    }

    @Operation(summary = "특정 경험 상세 조회", description = "사용자가 특정 경험을 클릭했을 때, 해당 경험에 대한 모든 정보를 가져옵니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXP001", description = "존재하지 않는 경험")
    })
    @GetMapping("/{experience_id}")
    public ApiResponse<DetailExperienceReadResponseDto> readExperience(@PathVariable("experience_id") Long experienceId) {
        return ApiResponse.onSuccess(experienceService.read(experienceId));
    }

    @Operation(summary = "특정 경험 업데이트", description = "사용자가 특정 필드를 수정하여 해당 경험을 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXP001", description = "존재하지 않는 경험"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CATEGORY001", description = "잘못된 카테고리"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXP002", description = "잘못된 경험 유형"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXP003", description = "잘못된 저장 유형"),
    })
    @PatchMapping("/{experience_id}")
    public ApiResponse<?> updateExperience(@PathVariable("experience_id") Long experienceId, @RequestBody ExperienceUpdateRequestDto experienceUpdateRequestDto) {
        experienceService.update(experienceId, experienceUpdateRequestDto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "특정 경험을 삭제", description = "사용자가 특정 경험을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXP001", description = "존재하지 않는 경험")
    })
    @DeleteMapping("/{experience_id}")
    public ApiResponse<?> deleteExperience(@PathVariable("experience_id") Long experienceId) {
        experienceService.delete(experienceId);
        return ApiResponse.onSuccess();
    }
}

