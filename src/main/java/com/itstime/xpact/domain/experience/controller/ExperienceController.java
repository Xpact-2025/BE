package com.itstime.xpact.domain.experience.controller;

import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.service.ExperienceService;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.response.ErrorResponse;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/exp")
@RequiredArgsConstructor
@Tag(name = "Experience API Controller", description = "경험 도메인 API")
public class ExperienceController {

    private final ExperienceService experienceService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "올바르지 않은 FormType", content = @Content(schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
                        {
                          "code": "EXP002",
                          "error": "INVALID_FORMTYPE",
                          "message": "FormType이 올바르지 않습니다."
                        }
                    """))),
    })
    @Operation(summary = "경험 생성", description = "주어진 데이터로 경험 생성")
    @PostMapping("/")
    public ResponseEntity<RestResponse<?>> createExperience(
            @RequestBody ExperienceCreateRequestDto createRequestDto)
    throws CustomException {

        experienceService.create(createRequestDto);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @Operation(summary = "경험 수정", description = "사용자가 특정 필드를 수정하여 해당 경험을 수정")
    @PatchMapping("/{experience_id}")
    public ResponseEntity<RestResponse<?>> updateExperience(
            @PathVariable("experience_id") Long experienceId,
            @RequestBody ExperienceUpdateRequestDto experienceUpdateRequestDto)
    throws CustomException {

        experienceService.update(experienceId, experienceUpdateRequestDto);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @Operation(summary = "경험 삭제", description = "사용자가 특정 경험을 삭제")
    @DeleteMapping("/{experience_id}")
    public ResponseEntity<RestResponse<?>> deleteExperience(
            @PathVariable("experience_id") Long experienceId)
    throws CustomException {

        experienceService.delete(experienceId);
        return ResponseEntity.ok(RestResponse.ok());
    }
}

