package com.diumi.backend.stepresult.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "단계 완료 요청")
public record CompleteStepRequest(
        @Schema(description = "해당 단계에 걸린 시간(ms)", example = "18500")
        @NotNull @PositiveOrZero Integer elapsedMs
) {
}
