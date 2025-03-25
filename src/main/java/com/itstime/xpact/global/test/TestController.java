package com.itstime.xpact.global.test;

import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Tag(name = "응답 테스트 API")
public class TestController {

    @Operation(summary = "API 응답 테스트 (데이터 X, 성공)", description = "데이터가 없는 성공한 API 응답을 내놓습니다.")
    @GetMapping("/no-data")
    public ApiResponse<?> testOnSuccess() {
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "API 응답 테스트 (데이터 O, 성공)", description = "데이터가 있는 성공한 API 응답을 내놓습니다.")
    @GetMapping("/data")
    public ApiResponse<?> testOnSuccessResult() {
        String message = "test result";
        return ApiResponse.onSuccess(message);
    }

    @Operation(summary = "API 응답 테스트 (실패)", description = "비즈니스 로직에서 실패한 API 응답을 내놓습니다.")
    @GetMapping("/failure")
    public ApiResponse<?> testOnFailure() {
        return ApiResponse.onFailure(ErrorCode.TEST);
    }
}
