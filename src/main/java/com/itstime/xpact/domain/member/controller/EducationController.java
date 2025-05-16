package com.itstime.xpact.domain.member.controller;

import com.itstime.xpact.domain.member.dto.request.EducationSaveRequestDto;
import com.itstime.xpact.domain.member.service.EducationService;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/educations")
@RequiredArgsConstructor
@Tag(name = "학력 API Controller", description = "학력사항 저장을 위한 API")
public class EducationController {

    private final EducationService educationService;

    // 학교 이름 정보 전체 반환(학교명 조회 기본)
    @Operation(summary = "학교명 조회 기본페이지 (전체 반환)",
    description = "학교명 조회에서 기본으로 불러오는 전체 학교 데이터의 리스트입니다.")
    @GetMapping("/name")
    public ResponseEntity<RestResponse<?>> findAllSchools(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(
                RestResponse.ok(
                        educationService.readSchoolNames()
                )
        );
    }

    // 학교 이름 검색
    @Operation(summary = "학교 이름 검색",
            description = """
                회원이 학교 이름을 검색하면 저장되어있는 학교의 이름이 조회됩니다.<br>
                학교의 이름만 반환되며 학과는 학교를 클릭시 조회하도록 합니다.<br>
                일치하는 이름이 없을 경우에는 null로 반환되며,<br>
                새로 직접 학교 이름을 입력하도록 설정해주세요.
                """)
    @GetMapping("/name/search")
    public ResponseEntity<RestResponse<?>> searchSchools(
            @RequestHeader("Authorization") String token,
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                RestResponse.ok(
                        educationService.autocompleteName(keyword)
                )
        );
    }

    // 학과명 조회
    @Operation(summary = "학과명 조회 기본페이지 (전체 반환)",
            description = """
                    학과명 조회에서 기본으로 불러오는 선택 학교의 학과명 데이터의 리스트입니다.<br>
                    학교 이름을 선택하였다면,<br>
                    해당 학교에 저장되어있는 학과들을 띄웁니다.
                    """)
    @GetMapping("/{name}/major")
    public ResponseEntity<RestResponse<?>> findAllMajors(
            @RequestHeader("Authorization") String token,
            @PathVariable String name
    ) {
        return ResponseEntity.ok(
                RestResponse.ok(
                        educationService.readMajor(name)
                )
        );
    }

    // 학과 선택
    @Operation(summary = "학과 이름 검색",
            description = """
                학과 리스트 중에서 검색어 입력에 일치하는 것들이 반환됩니다.<br>
                만약 학과가 존재하지 않을 경우 직접 입력하도록 처리해주세요.
                """)
    @GetMapping("/{name}/major/search")
    public ResponseEntity<RestResponse<?>> searchMajors(
            @RequestHeader("Authorization") String token,
            @PathVariable String name,
            @RequestParam String keyword
    ) {

        return ResponseEntity.ok(
                RestResponse.ok(
                    educationService.searchMajor(name, keyword)
                )
        );
    }

    // 학력 정보 저장
    @Operation(summary = "회원 정보에 반영될 학력 정보 저장",
    description = """
            입력 및 선택 정보를 통해 회원의 정보에 저장합니다.
            만약 학위 구분이 고등학교일 경우, 바로 이 API로 직접추가하는 형식으로 저장합니다.
            """)
    @PostMapping("")
    public ResponseEntity<RestResponse<?>> saveEducationInfo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "학력 정보 저장 DTO",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = EducationSaveRequestDto.class)
                    )
            )
            @RequestHeader("Authorization") String token,
            @RequestBody EducationSaveRequestDto requestDto
            ) {
        return ResponseEntity.ok(
                RestResponse.ok(
                        educationService.saveEducationInfo(requestDto)
                )
        );
    }

    // 최종학력 수정
    @Operation(summary = "학력 정보 업데이트", description = "최종 학력을 업데이트하는 API")
    @PatchMapping("")
    public ResponseEntity<RestResponse<?>> updateEducationInfo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "학력 정보 저장 DTO",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = EducationSaveRequestDto.class)
                    )
            )
            @RequestHeader("Authorization") String token,
            @RequestBody EducationSaveRequestDto requestDto
    ) {
        return ResponseEntity.ok(
                RestResponse.ok(
                        educationService.updateEducationInfo(requestDto)
                )
        );
    }
}
