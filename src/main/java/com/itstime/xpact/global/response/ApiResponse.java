package com.itstime.xpact.global.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"success", "code", "message", "data"})
public class ApiResponse<T> {

    private Boolean success;

    private String code;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    /**
     * 반환할 데이터가 없고, API응답이 성공인 경우 호출하는 메서드
     * @return ApiResponse(true, String.valueOf(HttpStatus.OK.value()), "OK", null)
     */
    public static ApiResponse<?> onSuccess() {
        return new ApiResponse<>(true, String.valueOf(HttpStatus.OK.value()), "OK", null);
    }

    /**
     * 반환할 데이터 (data)가 있고, API응답이 성공인 경우 호출하는 메서드
     * @param data
     * @return ApiResponse(true, String.valueOf(HttpStatus.OK.value()), "OK", data)
     */
    public static <T> ApiResponse<T> onSuccess(T data) {
        return new ApiResponse<>(true, String.valueOf(HttpStatus.OK.value()), "OK", data);
    }

    /**
     * 비즈니스 로직에서 예외 발생 시 호출하는 실패 메서드
     * @param errorCode
     * @return ApiResponse(false, errorCode.getCode(), errorCode.getMessage(), null)
     */
    public static <T> ApiResponse<T> onFailure(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }
}
