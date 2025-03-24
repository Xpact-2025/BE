package com.itstime.xpact.global.test;

import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/no-data")
    public ApiResponse<?> testOnSuccess() {
        return ApiResponse.onSuccess();
    }

    @GetMapping("/data")
    public ApiResponse<?> testOnSuccessResult() {
        String message = "test result";
        return ApiResponse.onSuccess(message);
    }

    @GetMapping("/failure")
    public ApiResponse<?> testOnFailure() {
        return ApiResponse.onFailure(ErrorCode.TEST);
    }
}
