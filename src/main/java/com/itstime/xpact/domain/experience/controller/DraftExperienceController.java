package com.itstime.xpact.domain.experience.controller;

import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.service.DraftExperienceService;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exp/draft")
@RequiredArgsConstructor
@Tag(name = "Experience (Status : DRAFT-임시저장) API Controller", description = "경험 (Status : DRAFT) 관련 API")
public class DraftExperienceController {

    private final DraftExperienceService draftExperienceService;

    @Operation(summary = "임시경험 생성", description = "주어진 데이터로 경험(임시저장)을 생성합니다.")
    @PostMapping("/")
    public ResponseEntity<RestResponse<?>> createDraftExperience(
            @RequestBody ExperienceCreateRequestDto createRequestDto)
    throws CustomException {

        draftExperienceService.create(createRequestDto);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @Operation(summary = "임시경험 수정", description = "사용자가 특정 필드를 수정하여 해당 경험(임시저장)을 수정합니다. 주의점은 임시저장상태의 경험을 수정하는 API입니다." +
            "저장상태의 경험을 수정할 수는 없습니다. 하지만, 임시저장상태의 경험을 수정하여 저장상태로 변경하는건 가능합니다.")
    @PatchMapping("/{experience_id}")
    public ResponseEntity<RestResponse<?>> updateDraftExperience(
            @PathVariable(name = "experience_id") Long experienceId,
            @RequestBody ExperienceUpdateRequestDto updateRequestDto)
    throws CustomException {

        draftExperienceService.update(experienceId, updateRequestDto);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @Operation(summary = "임시경험 삭제", description = "임시경험 삭제")
    @DeleteMapping("/{experience_id}")
    public ResponseEntity<RestResponse<?>> deleteDraftExperience(
            @PathVariable(name = "experience_id") Long experienceId)
    throws CustomException {

        draftExperienceService.delete(experienceId);
        return ResponseEntity.ok(RestResponse.ok());
    }
}
