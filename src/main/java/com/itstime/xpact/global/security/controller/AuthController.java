package com.itstime.xpact.global.security.controller;

import com.itstime.xpact.global.response.ApiResponse;
import com.itstime.xpact.global.security.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/auth")
@RequiredArgsConstructor
@Tag(name = "폼 로그인")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/singup")
    public void signup(
            // TODO: DTO 생성
    ) {
    }
}
