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
    @Operation(summary = "회원 정보 입력 API")
    @PostMapping("")
    @PatchMapping("")
    public ResponseEntity<RestResponse<?>> saveMyInfo(
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
