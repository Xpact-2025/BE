package com.itstime.xpact.domain.experience.controller;

import com.itstime.xpact.domain.experience.dto.ExperienceCreateRequestDto;
import com.itstime.xpact.domain.experience.dto.ExperienceUpdateRequestDto;
import com.itstime.xpact.domain.experience.service.DraftExperienceService;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exp/draft")
@RequiredArgsConstructor
@Tag(name = "Experience (DRAFT) API Controller", description = "경험(임시저장) API")
public class DraftExperienceController {

    private final DraftExperienceService draftExperienceService;

    @Operation(summary = "임시경험 생성", description = "주어진 데이터로 경험 생성 & 요약 생성 X (임시저장이므로)")
    @PostMapping("/")
    public ResponseEntity<RestResponse<?>> createDraftExperience(
            @RequestBody ExperienceCreateRequestDto createRequestDto) {
        draftExperienceService.create(createRequestDto);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @Operation(summary = "임시경험 수정", description = "주어진 데이터로 임시경험 수정 & 요약 생성 X (임시저장이므로)")
    @PatchMapping("/{experience_id}")
    public ResponseEntity<RestResponse<?>> updateDraftExperience(
            @PathVariable(name = "experience_id") Long experienceId,
            @RequestBody ExperienceUpdateRequestDto updateRequestDto) {
        draftExperienceService.update(experienceId, updateRequestDto);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @Operation(summary = "임시경험 삭제", description = "임시경험 삭제")
    @DeleteMapping("/{experience_id}")
    public ResponseEntity<RestResponse<?>> deleteDraftExperience(
            @PathVariable(name = "experience_id") Long experienceId) {
        draftExperienceService.delete(experienceId);
        return ResponseEntity.ok(RestResponse.ok());
    }
}
