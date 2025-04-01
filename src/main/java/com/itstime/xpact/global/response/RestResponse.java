package com.itstime.xpact.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestResponse<T> {

    @Schema(example = "200")
    private int httpStatus;
    @Schema(example = "success")
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    /**
     * 200 OK
     */
    public static<T> RestResponse<T> ok(final T data) {
        return RestResponse.<T>builder()
                .httpStatus(200)
                .message("success")
                .data(data)
                .build();
    }

    public static RestResponse<Void> ok() {
        return RestResponse.<Void>builder()
                .httpStatus(200)
                .message("success")
                .build();
    }
}
