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
@Tag(name = "Experience (Status : SAVE-저장) API Controller", description = "경험 (Status : SAVE) 관련 API")
public class ExperienceController {

    private final ExperienceService experienceService;

    @Operation(summary = "경험 생성", description = "주어진 데이터로 경험 생성 (사용자 정보는 인가처리된 사용자 정보 사용)")
    @PostMapping("/")
    public ResponseEntity<RestResponse<?>> createExperience(
            @RequestBody ExperienceCreateRequestDto createRequestDto)
    throws CustomException {

        experienceService.create(createRequestDto);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @Operation(summary = "경험 수정", description = "사용자가 특정 필드를 수정하여 해당 경험을 수정합니다.주의점은 저장상태의 경험을 수정하는 API입니다." +
            "임시저장상태의 경험을 수정할 수는 없습니다. 하지만, 저장상태의 경험을 수정하여 임시저장상태로 변경하는건 가능합니다.")
    @PatchMapping("/{experience_id}")
    public ResponseEntity<RestResponse<?>> updateExperience(
            @PathVariable("experience_id") Long experienceId,
            @RequestBody ExperienceUpdateRequestDto experienceUpdateRequestDto)
    throws CustomException {

        experienceService.update(experienceId, experienceUpdateRequestDto);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @Operation(summary = "경험 삭제", description = "사용자가 특정 경험을 삭제합니다.")
    @DeleteMapping("/{experience_id}")
    public ResponseEntity<RestResponse<?>> deleteExperience(
            @PathVariable("experience_id") Long experienceId)
    throws CustomException {

        experienceService.delete(experienceId);
        return ResponseEntity.ok(RestResponse.ok());
    }
}

