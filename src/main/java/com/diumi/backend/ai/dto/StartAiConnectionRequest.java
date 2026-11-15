package com.diumi.backend.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;

@Schema(description = "AI 음성 연결 시작 요청")
public record StartAiConnectionRequest(
        @Schema(description = "현재 단계 ID", example = "14")
        @NotNull @Positive Long stepId,
        @Schema(description = "현재 화면 코드. AI가 화면에 맞는 안내 규칙을 선택할 때 사용하며 생략 가능", example = "SELECT_PAYMENT")
        @Pattern(regexp = "^[A-Z][A-Z0-9_]*$") String screenCode
) {
}
