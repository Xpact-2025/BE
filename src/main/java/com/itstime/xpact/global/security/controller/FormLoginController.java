package com.itstime.xpact.global.security.controller;

import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.response.ErrorResponse;
import com.itstime.xpact.global.response.RestResponse;
import com.itstime.xpact.global.security.dto.request.LoginRequestDto;
import com.itstime.xpact.global.security.dto.request.SignupRequestDto;
import com.itstime.xpact.global.security.dto.response.SignupResponseDto;
import com.itstime.xpact.global.security.service.FormLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "폼 로그인 API Controller", description = "일반 로그인 API")
public class FormLoginController {

    private final FormLoginService formLoginService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "올바르지 않은 로그인", content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = """
                        {
                          "code": "MEMBER001",
                          "error": "MEMBER_NOT_EXISTS",
                          "message": "이미 존재하는 회원입니다."
                        }
                    """))),
    })
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


    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "올바르지 않은 회원가입", content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = """
                        {
                          "code": "MEMBER002",
                          "error": "MEMBER_ALREADEY_EXISTS",
                          "message": "이미 존재하는 회원입니다."
                        }
                    """))),
    })
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


    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "올바르지 않은 로그아웃", content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = """
                        {
                          "code": "TE001",
                          "error": "TOKEN_NOT_FOUND",
                          "message": "토큰이 존재하지 않습니다."
                        }
                    """))),
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "올바르지 않은 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = """
                        {
                          "code": "CE001",
                          "error": "EMPTY_COOKIE",
                          "message": "쿠키가 존재하지 않습니다."
                        }
                    """))),
    })
    @Operation(summary = "액세스 토큰 재발급", description = """
            Access Token이 만료되면,<br>
            Refresh Token을 이용하여 Access Token을 재발급 받습니다.<br>
            쿠키에 저장된 Refresh Token을 이용하여,<br>
            Access Token을 반환합니다.
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
