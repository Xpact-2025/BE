package com.itstime.xpact.domain.member.controller;

import com.itstime.xpact.domain.member.dto.request.SchoolSaveRequestDto;
import com.itstime.xpact.domain.member.service.SchoolService;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/schools")
@RequiredArgsConstructor
@Tag(name = "School API Controller", description = "학교 이름 조회를 위한 API")
public class SchoolController {

    private final SchoolService schoolService;

    // 학교 이름 검색
    @Operation(summary = "학교 이름 검색",
            description = """
                회원이 학교 이름을 검색하면 저장되어있는 학교의 이름이 조회됩니다.<br>
                학교의 이름만 반환되며 학과는 학교를 클릭시 조회하도록 합니다.<br>
                일치하지 이름이 없을 경우에는 null로 반환되며,<br>
                새로 직접 학교 이름을 입력하도록 설정해주세요.
                """)
    @GetMapping("/name")
    public ResponseEntity<RestResponse<?>> searchSchools(
            @RequestHeader("Authorization") String authToken,
            @RequestParam String keyword) {

        String token = authToken.startsWith("Bearer ") ? authToken.substring(7) : authToken;

        return ResponseEntity.ok(
                RestResponse.ok(
                        schoolService.autocompleteName(keyword)
                )
        );
    }

    // 학과 선택
    @Operation(summary = "학과 이름 검색",
            description = """
                학교 이름을 선택하였다면,<br>
                해당 학교에 저장되어있는 학과들을 띄웁니다.<br>
                여기서 선택한 정보는 회원 정보의 학력으로 저장됩니다.<br>
                만약 학과가 존재하지 않을 경우 직접 입력하도록 처리해주세요.
                """)
    @GetMapping("/{name}/major")
    public ResponseEntity<RestResponse<?>> searchMajors(
            @RequestHeader("Authorization") String authToken,
            @PathVariable String name
    ) {

        return ResponseEntity.ok(
                RestResponse.ok(
                    schoolService.searchMajor(name)
                )
        );
    }

    // 학력 정보 저장
    @PostMapping("")
    public ResponseEntity<RestResponse<?>> saveSchoolInfo(
            @RequestHeader("Authorization") String authToken,
            @RequestBody SchoolSaveRequestDto requestDto
            ) {
        return ResponseEntity.ok(
                RestResponse.ok(
                        schoolService.saveSchoolInfo(requestDto)
                )
        );
    }
}
