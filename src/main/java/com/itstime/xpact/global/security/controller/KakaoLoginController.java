package com.itstime.xpact.global.security.controller;

import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.response.ErrorResponse;
import com.itstime.xpact.global.response.RestResponse;
import com.itstime.xpact.global.security.service.KakaoLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "카카오 로그인 API Controller", description = "카카오 로그인 API")
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    @Operation(summary = "카카오 인증코드 받기", description = "카카오 인증코드를 받을 수 있는 url을 반환합니다.")
    @GetMapping("/url/kakao")
    public ResponseEntity<String> get() {
        return ResponseEntity.ok(kakaoLoginService.getLoginUrl());
    }


    @Operation(summary = "카카오 로그인하기", description = "카카오 로그인 기능의 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "카카오 서버로의 요청 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = """
                            {
                            "code": "LE001"
                            "error": "PLATFORM_NOT_FOUND",
                            "message": "해당 플랫폼이 존재하지 않습니다."
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "카카오 서버로부터 토큰 요청 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = """
                        {
                          "code": "LE002",
                          "error": "ACCESS_TOKEN_REQUEST_FAILED",
                          "message": "서버로부터 토큰을 받지 못하였습니다."
                        }
                    """))),
            @ApiResponse(responseCode = "401", description = "카카오 서버로부터 토큰 요청 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = """
                            {
                            "code": "LE003",
                            "error": "FAILED_MEMBER_INFO",
                            "message": "서버로부터 프로필을 조회하지 못하였습니다."
                            }
                            """)))
    })
    @PostMapping("/login/kakao")
    public ResponseEntity<RestResponse<?>> callback(
            @Parameter(description = "카카오로부터 얻은 인증 코드")
            @RequestParam String code,
            HttpServletResponse response) throws CustomException {
        return ResponseEntity.ok(
                RestResponse.ok(kakaoLoginService.login(code, response)));
    }
}
