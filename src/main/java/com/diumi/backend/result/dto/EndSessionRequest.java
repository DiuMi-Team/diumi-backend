package com.diumi.backend.result.dto;

import com.diumi.backend.session.entity.SessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "체험 세션 종료 요청")
public record EndSessionRequest(
        @Schema(description = "종료 상태. 정상 완료 또는 중도 포기만 허용", example = "COMPLETED", allowableValues = {"COMPLETED", "ABANDONED"})
        @NotNull SessionStatus status
) {
}
