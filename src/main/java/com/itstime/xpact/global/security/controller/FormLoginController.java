package com.itstime.xpact.global.security.controller;

import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.response.RestResponse;
import com.itstime.xpact.global.security.dto.request.LoginRequestDto;
import com.itstime.xpact.global.security.dto.request.SignupRequestDto;
import com.itstime.xpact.global.security.dto.response.SignupResponseDto;
import com.itstime.xpact.global.security.service.FormLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class FormLoginController {

    private final FormLoginService formLoginService;

    @Operation(summary = "로그인하기", description = "로그인 기능의 API 입니다.", requestBody =
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "이메일 주소와 비밀번호",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = LoginRequestDto.class)
            )))
    @PostMapping("/login/form")
    public ResponseEntity<RestResponse<?>> formLogin(
            @RequestBody LoginRequestDto requestDto,
            HttpServletResponse response
    ) throws CustomException {
        return ResponseEntity.ok(
                RestResponse.ok(
                        formLoginService.generalLogin(requestDto, response)
                ));
    }

    @Operation(summary = "회원 가입하기", description = "회원가입 API 입니다.", requestBody =
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "회원 가입 요청 객체",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = SignupRequestDto.class)
            )))
    @PostMapping("/signup")
    public ResponseEntity<RestResponse<SignupResponseDto>> signup (
            @RequestBody SignupRequestDto signupRequestDto
    ) throws CustomException{

        return ResponseEntity.ok(
                RestResponse.ok(
                        formLoginService.register(signupRequestDto))
        );
    }

    @Operation(summary = "로그아웃", description = "로그아웃 기능 API 입니다.")
    @PostMapping("/logout")
    public ResponseEntity<RestResponse<?>> logout(
            HttpServletResponse response,
            @RequestHeader("Authorization") String authToken
    ) {
        String token = authToken.startsWith("Bearer ") ?
                authToken.substring(7) : authToken;

        formLoginService.logout(response, token);

        return ResponseEntity.ok(
                RestResponse.ok("성공적으로 로그아웃 되었습니다.")
        );
    }

    // Access Token 재발급받기
    @Operation(summary = "액세스 토큰 재발급", description = """
            Access Token이 만료되면,<br>
            Refresh Token을 이용하여 Access Token을 재발급 받습니다.
            """)
    @PostMapping("/refresh")
    public ResponseEntity<RestResponse<?>> refresh(
            HttpServletRequest request, HttpServletResponse response
    ) throws CustomException {
        return ResponseEntity.ok(
                RestResponse.ok(
                        formLoginService.refresh(request, response)));
    }
}
