package com.itstime.xpact.domain.member.controller;

import com.itstime.xpact.domain.member.service.MemberService;
import com.itstime.xpact.global.response.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 마이페이지 조회
    @GetMapping
    public ResponseEntity<RestResponse<?>> mypage(
            @RequestHeader("Authorization") String authToken
    ) {
       return ResponseEntity.ok(
               RestResponse.ok(
                       memberService.getMyinfo()
               )
       );
    }

}
