package com.itstime.xpact.domain.experience.controller;

import com.itstime.xpact.domain.experience.common.Status;
import com.itstime.xpact.domain.experience.dto.request.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.request.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.entity.Experience;
import com.itstime.xpact.domain.experience.entity.SubExperience;
import com.itstime.xpact.domain.experience.service.ExperienceService;
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
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/exp")
@RequiredArgsConstructor
@Tag(name = "Experience API Controller", description = "경험 도메인 API")
public class ExperienceController {

    private final ExperienceService experienceService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "경험 생성 시 발생하는 에러 유형", content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                        @ExampleObject(name = "INVALID_FORMTYPE", value = """
                        {
                              "httpStatus": "BAD_REQUEST",
                              "code": "EXP002",
                              "message": "잘못된 FormType입니다."
                        }"""),
                        @ExampleObject(name = "INVALID_STATUS", value = """
                        {
                              "httpStatus": "BAD_REQUEST",
                              "code": "EXP003",
                              "message": "잘못된 Status입니다."
                        }"""),
                        @ExampleObject(name = "INVALID_EXPERIENCE_TYPE", value = """
                        {
                              "httpStatus": "BAD_REQUEST",
                              "code": "EXP007",
                              "message": "잘못된 ExperienceType입니다."
                        }"""),
                    }
            ))
    })
    @Operation(summary = "경험 생성", description = "주어진 데이터로 경험 생성")
    @PostMapping("")
    public ResponseEntity<RestResponse<?>> createExperience(
            @RequestBody ExperienceCreateRequestDto createRequestDto)
    throws GeneralException {

        Pair<Experience, List<SubExperience>> expPair = experienceService.create(createRequestDto);
        if(Status.valueOf(createRequestDto.getStatus()).equals(Status.SAVE)) experienceService.setSummaryAndDetailRecruit(expPair);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "경험 수정 시 발생하는 에러 유형", content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                        @ExampleObject(name = "INVALID_FORMTYPE", value = """
                        {
                              "httpStatus": "BAD_REQUEST",
                              "code": "EXP002",
                              "message": "잘못된 FormType입니다."
                        }"""),
                        @ExampleObject(name = "INVALID_STATUS", value = """
                        {
                              "httpStatus": "BAD_REQUEST",
                              "code": "EXP003",
                              "message": "잘못된 Status입니다."
                        }"""),
                        @ExampleObject(name = "INVALID_EXPERIENCE_TYPE", value = """
                        {
                              "httpStatus": "BAD_REQUEST",
                              "code": "EXP007",
                              "message": "잘못된 ExperienceType입니다."
                        }"""),

                        @ExampleObject(name = "EXPERIENCE_NOT_EXISTS", value = """
                            {
                              "httpStatus": "BAD_REQUEST",
                              "code": "EXP001",
                              "message": "해당 경험이 존재하지 않습니다."
                            }
                        """),
                        @ExampleObject(name = "NOT_YOUR_EXPERIENCE", value = """
                            {
                              "httpStatus": "BAD_REQUEST",
                              "code": "EXP005",
                              "message": "본인의 Experience가 아닙니다."
                            }
                        """),
                        @ExampleObject(name = "INVALID_SAVE", value = """
                            {
                              "httpStatus": "BAD_REQUEST",
                              "code": "EXP008",
                              "message": "저장된 경험은 임시저장될 수 없습니다."
                            }
                        """),
                    }
            ))
    })
    @Operation(summary = "경험 수정", description = "사용자가 특정 필드를 수정하여 해당 경험을 수정")
    @PatchMapping("/{experience_id}")
    public ResponseEntity<RestResponse<?>> updateExperience(
            @PathVariable("experience_id") Long experienceId,
            @RequestBody ExperienceUpdateRequestDto experienceUpdateRequestDto)
    throws GeneralException {

        Pair<Experience, List<SubExperience>> expPair = experienceService.update(experienceId, experienceUpdateRequestDto);
        if(Status.valueOf(experienceUpdateRequestDto.getStatus()).equals(Status.SAVE)) experienceService.setSummaryAndDetailRecruit(expPair);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "경험 삭제 시 발생하는 에러 유형", content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "EXPERIENCE_NOT_EXISTS",
                                    value = """
                            {
                              "code": "EXP001",
                              "error": "EXPERIENCE_NOT_EXISTS",
                              "message": "해당 경험이 존재하지 않습니다."
                            }
                        """),
                            @ExampleObject(name = "NOT_YOUR_EXPERIENCE",
                                    value = """
                            {
                              "code": "EXP005",
                              "error": "NOT_YOUR_EXPERIENCE",
                              "message": "본인의 Experience가 아닙니다."
                            }
                        """)
                    }
            ))
    })
    @Operation(summary = "경험 삭제", description = "사용자가 특정 경험을 삭제")
    @DeleteMapping("/{experience_id}")
    public ResponseEntity<RestResponse<?>> deleteExperience(
            @PathVariable("experience_id") Long experienceId)
    throws GeneralException {

        experienceService.delete(experienceId);
        return ResponseEntity.ok(RestResponse.ok());
    }
}
