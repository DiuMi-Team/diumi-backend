package com.diumi.backend.session.dto;

import com.diumi.backend.session.entity.SessionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "체험 세션 시작 요청")
public record StartSessionRequest(
        @Schema(description = "시나리오 ID", example = "1")
        @NotNull Long scenarioId,
        @Schema(description = "세션 유형", example = "REAL_MISSION", allowableValues = {"PRACTICE_MISSION", "REAL_MISSION", "REAL_FREE"})
        @NotNull SessionType sessionType,
        @Schema(description = "미션 ID. REAL_FREE에서는 생략하고 나머지 유형에서는 필수", example = "101")
        Long missionId
) {
}
