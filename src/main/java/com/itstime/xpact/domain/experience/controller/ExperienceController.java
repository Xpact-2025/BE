package com.itstime.xpact.domain.experience.controller;

import com.itstime.xpact.domain.experience.dto.DetailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.dto.ThumbnailExperienceReadResponseDto;
import com.itstime.xpact.domain.experience.service.ExperienceService;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "CATEGORY001", description = "잘못된 카테고리"),
            @ApiResponse(responseCode = "EXP002", description = "잘못된 유형")
    })
    @PostMapping("/")
    public ResponseEntity<RestResponse<?>> createExperience(@RequestBody ExperienceCreateRequestDto createRequestDto) throws CustomException {

        experienceService.create(createRequestDto);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @Operation(summary = "사용자의 모든 경험 조회", description = "사용자가 작성한 모든 경험을 조회합니다. (임시저장, 저장 모두 조회), (페이지 처리 X), (상세 조회 X)")
    @GetMapping("/")
    public ResponseEntity<RestResponse<List<ThumbnailExperienceReadResponseDto>>> readAllExperience() throws CustomException {
        // TODO : Swagger 확인 후 ListResponse 따로 생성할지에 대하여 고민
        return ResponseEntity.ok(
                RestResponse.ok(experienceService.readAll())
        );
    }

    @Operation(summary = "특정 경험 상세 조회", description = "사용자가 특정 경험을 클릭했을 때, 해당 경험에 대한 모든 정보를 가져옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "EXP001", description = "존재하지 않는 경험")
    })
    @GetMapping("/{experience_id}")
    public ResponseEntity<RestResponse<DetailExperienceReadResponseDto>> readExperience(@PathVariable("experience_id") Long experienceId)
    throws CustomException {
        return ResponseEntity.ok(
                RestResponse.ok(experienceService.read(experienceId))
        );
    }

    @Operation(summary = "특정 경험 업데이트", description = "사용자가 특정 필드를 수정하여 해당 경험을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "EXP001", description = "존재하지 않는 경험"),
            @ApiResponse(responseCode = "CATEGORY001", description = "잘못된 카테고리"),
            @ApiResponse(responseCode = "EXP002", description = "잘못된 경험 유형"),
            @ApiResponse(responseCode = "EXP003", description = "잘못된 저장 유형"),
    })
    @PatchMapping("/{experience_id}")
    public ResponseEntity<RestResponse<?>> updateExperience(@PathVariable("experience_id") Long experienceId, @RequestBody ExperienceUpdateRequestDto experienceUpdateRequestDto)
    throws CustomException {

        experienceService.update(experienceId, experienceUpdateRequestDto);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @Operation(summary = "특정 경험을 삭제", description = "사용자가 특정 경험을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "EXP001", description = "존재하지 않는 경험")
    })
    @DeleteMapping("/{experience_id}")
    public ResponseEntity<RestResponse<?>> deleteExperience(@PathVariable("experience_id") Long experienceId)
    throws CustomException {

        experienceService.delete(experienceId);
        return ResponseEntity.ok(RestResponse.ok());
    }
}

