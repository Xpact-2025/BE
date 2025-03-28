package com.itstime.xpact.global.security.controller;

import com.itstime.xpact.global.response.ApiResponse;
import com.itstime.xpact.global.security.dto.request.LoginRequestDto;
import com.itstime.xpact.global.security.dto.request.SignupRequestDto;
import com.itstime.xpact.global.security.dto.response.SignupResponseDto;
import com.itstime.xpact.global.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "폼 로그인 API")
public class AuthController {

    private final AuthService authService;


    @Operation(summary = "회원 가입하기", description = "회원가입 API 입니다.", requestBody =
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "회원 가입 요청 객체",
    required = true,
    content = @Content(
            schema = @Schema(implementation = SignupRequestDto.class)
    )))
    @PostMapping("/signup")
    public ApiResponse<SignupResponseDto> signup(
            @RequestBody SignupRequestDto signupRequestDto
            ) {

        return ApiResponse.onSuccess(
                authService.register(signupRequestDto)
        );
    }

    @Operation(summary = "로그인하기", description = "로그인 기능의 API 입니다.", requestBody =
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "이메일 주소와 비밀번호",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = LoginRequestDto.class)
            )))
    @PostMapping("/login")
    public ApiResponse<?> login(
            @RequestBody LoginRequestDto requestDto,
            HttpServletResponse response
            ) {
        return ApiResponse.onSuccess(
                authService.generalLogin(requestDto, response)
        );
    }

    @Operation(summary = "로그아웃", description = "로그아웃 기능 API 입니다.")
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

    // Access Token 재발급받기
    @Operation(summary = "액세스 토큰 재발급", description = """
            Access Token이 만료되면,<br>
            Refresh Token을 이용하여 Access Token을 재발급 받습니다.
            """)
    @PostMapping("/refresh")
    public ApiResponse<?> refresh(
            HttpServletRequest request, HttpServletResponse response
    ) {
        return ApiResponse.onSuccess(authService.refresh(request, response));
    }

    // 회원 탈퇴하기
}
