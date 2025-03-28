package com.itstime.xpact.global.security.controller;

import com.itstime.xpact.global.response.ApiResponse;
import com.itstime.xpact.global.security.dto.request.LoginRequestDto;
import com.itstime.xpact.global.security.dto.request.SignupRequestDto;
import com.itstime.xpact.global.security.dto.response.SignupResponseDto;
import com.itstime.xpact.global.security.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/logout")
    public ApiResponse<?> logout(
            HttpServletResponse response,
            @RequestHeader("Authorization") String authToken
    ) {
        String token = authToken.startsWith("Bearer ") ?
                authToken.substring(7) : authToken;

        authService.logout(response, token);

        return ApiResponse.onSuccess("성공적으로 로그아웃 되었습니다.");
    }

    // 회원 탈퇴하기
}
