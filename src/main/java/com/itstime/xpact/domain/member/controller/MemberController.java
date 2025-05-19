package com.itstime.xpact.domain.member.controller;

import com.itstime.xpact.domain.member.dto.request.MemberSaveRequestDto;
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
@Tag(name = "회원 API Controller", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 저장 API", description = """
            회원 정보 저장을 위한 API 입니다.<br>
            희망직무, 학력에 대한 저장도 함께 요청이 가능합니다.<br>
            """)
    @PatchMapping()
    public ResponseEntity<?> saveMemberProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody MemberSaveRequestDto requestDto
            ) {
        return ResponseEntity.ok(
                RestResponse.ok(memberService.updateMember(requestDto)
                ));
    }

    @Operation(summary = "회원 정보 조회 API", description = """
            회원 정보 조회를 위한 API입니다.<br>
            필요한 정보들에 대하여 파싱하면 됩니다.
            """)
    @GetMapping()
    public ResponseEntity<?> getMemberProfile(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(
                RestResponse.ok(
                        memberService.getMember()
                )
        );
    }
}
