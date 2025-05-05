package com.itstime.xpact.domain.member.controller;

import com.itstime.xpact.domain.member.dto.request.MemberInfoRequestDto;
import com.itstime.xpact.domain.member.service.MemberService;
import com.itstime.xpact.global.response.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member API Controller", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;

    // 마이페이지 조회
    @Operation(summary = "마이페이지 조회 API")
    @GetMapping("")
    public ResponseEntity<RestResponse<?>> getMyInfo(
            @RequestHeader("Authorization") String authToken
    ) {
       return ResponseEntity.ok(
               RestResponse.ok(
                       memberService.getMyinfo()
               )
       );
    }

    // 회원 정보 입력
    @Operation(summary = "회원 정보 입력 API", description = """
            회원 프로필의 정보를 등록합니다.<br>
            이름, 사진, 나이에 대한 정보에 대한 등록입니다.<br>
            학력 및 희망 직무에 대한 저장은 다른 요청을 보내주세요.<br>
            """)
    @PatchMapping("")
    public ResponseEntity<RestResponse<?>> saveMyInfo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "회원 정보 등록 DTO", required = true)
            @RequestHeader("Authorization") String authToken,
            @RequestBody MemberInfoRequestDto requestDto
            ) {
        return ResponseEntity.ok(
                RestResponse.ok(
                        memberService.saveMyinfo(requestDto)
                )
        );
    }
}
