package com.diumi.backend.common.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API 오류 정보")
public record ApiError(
        @Schema(description = "클라이언트가 분기 처리할 오류 코드", example = "SCENARIO_NOT_FOUND")
        String code,
        @Schema(description = "사용자에게 표시할 수 있는 오류 메시지", example = "서비스를 찾을 수 없어요.")
        String message
) {
}
