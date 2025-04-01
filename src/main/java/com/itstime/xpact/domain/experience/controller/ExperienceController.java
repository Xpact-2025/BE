package com.itstime.xpact.domain.experience.controller;

import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.service.ExperienceService;
import com.itstime.xpact.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exp")
@RequiredArgsConstructor
@Tag(name = "Experience (SAVE) API Controller", description = "경험(저장) API")
public class ExperienceController {

    private final ExperienceService experienceService;


    @Operation(summary = "경험 생성", description = "주어진 데이터로 경험 생성 & 요약된 내용 자동 생성됨 (저장이므로)")
    @PostMapping("/")
    public ApiResponse<?> createExperience(@RequestBody ExperienceCreateRequestDto createRequestDto) {
        experienceService.create(createRequestDto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "특정 경험 업데이트", description = "사용자가 특정 필드를 수정하여 해당 경험을 수정합니다.")
    @PatchMapping("/{experience_id}")
    public ApiResponse<?> updateExperience(@PathVariable("experience_id") Long experienceId,
                                           @RequestBody ExperienceUpdateRequestDto experienceUpdateRequestDto) {
        experienceService.update(experienceId, experienceUpdateRequestDto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "특정 경험을 삭제", description = "사용자가 특정 경험을 삭제합니다.")
    @DeleteMapping("/{experience_id}")
    public ApiResponse<?> deleteExperience(@PathVariable("experience_id") Long experienceId) {
        experienceService.delete(experienceId);
        return ApiResponse.onSuccess();
    }
}

