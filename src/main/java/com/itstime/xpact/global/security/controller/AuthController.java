package com.itstime.xpact.global.security.controller;

import com.itstime.xpact.global.response.ApiResponse;
import com.itstime.xpact.global.security.dto.request.LoginRequestDto;
import com.itstime.xpact.global.security.dto.request.SignupRequestDto;
import com.itstime.xpact.global.security.dto.response.SignupResponseDto;
import com.itstime.xpact.global.security.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "폼 로그인")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<SignupResponseDto> signup(
            @RequestBody SignupRequestDto signupRequestDto
            ) {

        return ApiResponse.onSuccess(
                authService.register(signupRequestDto)
        );
    }

    @PostMapping("/login")
    public ApiResponse<?> login(
            @RequestBody LoginRequestDto requestDto,
            HttpServletResponse response
            ) {
        return ApiResponse.onSuccess(
                authService.generalLogin(requestDto, response)
        );
    }
}
