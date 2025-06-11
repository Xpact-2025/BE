package com.itstime.xpact.domain.resume.controller;

import com.itstime.xpact.domain.experience.dto.response.RecommendExperienceResponseDto;
import com.itstime.xpact.domain.resume.dto.request.AiResumeRequestDto;
import com.itstime.xpact.domain.resume.dto.request.CreateResumeRequestDto;
import com.itstime.xpact.domain.resume.dto.request.RecommendExperienceRequestDto;
import com.itstime.xpact.domain.resume.dto.request.UpdateResumeRequestDto;
import com.itstime.xpact.domain.resume.dto.response.DetailResumeResponseDto;
import com.itstime.xpact.domain.resume.dto.response.ThumbnailResumeResponseDto;
import com.itstime.xpact.domain.resume.service.ResumeService;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @Operation(description = "자기소개서 저장", summary = "AI로 생성된 자기소개서를 저장합니다.")
    @PostMapping("")
    public ResponseEntity<RestResponse<?>> createResume(
            @RequestBody CreateResumeRequestDto createResumeRequestDto) {
        resumeService.create(createResumeRequestDto);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @Operation(description = "자기소개서 수정", summary = "저장된 자기소개서를 수정합니다.")
    @PatchMapping("/{resume_id}")
    public ResponseEntity<RestResponse<?>> updateResume(
            @PathVariable("resume_id") Long resumeId,
            @RequestBody UpdateResumeRequestDto updateResumeRequestDto) {
        resumeService.update(resumeId, updateResumeRequestDto);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @Operation(description = "자기소개서 삭제", summary = "저장된 자기소개서를 삭제합니다.")
    @DeleteMapping("/{resume_id}")
    public ResponseEntity<RestResponse<?>> deleteResume(
            @PathVariable("resume_id") Long resumeId) {
        resumeService.delete(resumeId);
        return ResponseEntity.ok(RestResponse.ok());
    }

    @Operation(description = "자기소개서 조회", summary = "저장된 자기소개서를 조회합니다.")
    @GetMapping("/{resume_id}")
    public ResponseEntity<RestResponse<?>> getResume(
            @PathVariable("resume_id") Long resumeId) {
        DetailResumeResponseDto responseDto = resumeService.read(resumeId);
        return ResponseEntity.ok(RestResponse.ok(responseDto));
    }

    @Operation(description = "자기소개서 목록 조회", summary = "사용자의 모든 자기소개서를 조회합니다.")
    @GetMapping("")
    public ResponseEntity<RestResponse<List<ThumbnailResumeResponseDto>>> getAllResume() {
        return ResponseEntity.ok(RestResponse.ok(resumeService.readAll()));
    }

    @Operation(description = "추천 경험 조회", summary = "제목, 문항을 통해 자기소개서의 기반이 될 경험을 추천합니다.")
    @PostMapping("/ai-experience")
    public ResponseEntity<RestResponse<List<RecommendExperienceResponseDto>>> generateAiResume(
            @RequestBody RecommendExperienceRequestDto requestDto) {
        return ResponseEntity.ok(RestResponse.ok(resumeService.getRecommendExperience(requestDto)));
    }

    @Operation(description = "AI자기소개서 생성", summary = "작성한 제목, 문항, 경험, 키워드 등을 분석하여 자기소개서를 생성합니다.")
    @GetMapping("/ai-generate")
    public ResponseEntity<RestResponse<?>> getAiRecommendExperience(
            @RequestBody AiResumeRequestDto aiResumeRequestDto) {
        return ResponseEntity.ok(RestResponse.ok(resumeService.createResume(aiResumeRequestDto)));
    }
}
