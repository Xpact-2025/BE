package com.itstime.xpact.global.security.controller;

import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.response.RestResponse;
import com.itstime.xpact.global.security.service.KakaoLoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    @PostMapping("/login/kakao")
    public ResponseEntity<RestResponse<?>> callback(
            String code,
            HttpServletResponse response) throws CustomException {
        return ResponseEntity.ok(
                RestResponse.ok(kakaoLoginService.login(code, response)));
    }
}
