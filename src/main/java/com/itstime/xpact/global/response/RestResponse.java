package com.itstime.xpact.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itstime.xpact.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestResponse<T> {

    @Schema(example = "true")
    private boolean isSuccess;
    @Schema(example = "200")
    private int httpStatus;
    @Schema(example = "success")
    private String message;
    @Schema(example = "C001")
    private String code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    /**
     * 200 OK
     */
    public static<T> RestResponse<T> ok(final T data) {
        return RestResponse.<T>builder()
                .isSuccess(true)
                .httpStatus(200)
                .message("success")
                .data(data)
                .build();
    }

    public static RestResponse<Void> ok() {
        return RestResponse.<Void>builder()
                .isSuccess(true)
                .httpStatus(200)
                .message("success")
                .build();
    }

    public static RestResponse<Void> onFailure(ErrorCode errorCode) {
        return RestResponse.<Void>builder()
                .isSuccess(false)
                .httpStatus(200)
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .build();
    }
}
