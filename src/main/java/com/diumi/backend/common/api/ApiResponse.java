package com.diumi.backend.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.ALWAYS)
@Schema(description = "공통 API 응답")
public record ApiResponse<T>(
        @Schema(description = "요청 성공 여부", example = "true") boolean success,
        @Schema(description = "성공 응답 데이터. 실패하면 null") T data,
        @Schema(description = "오류 정보. 성공하면 null") ApiError error
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> failure(String code, String message) {
        return new ApiResponse<>(false, null, new ApiError(code, message));
    }
}
