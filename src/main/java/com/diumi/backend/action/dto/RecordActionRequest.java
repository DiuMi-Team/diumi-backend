package com.diumi.backend.action.dto;

import com.diumi.backend.action.entity.ActionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import tools.jackson.databind.JsonNode;

@Schema(description = "사용자 행동 기록 요청")
public record RecordActionRequest(
        @Schema(description = "행동이 발생한 현재 단계 ID", example = "11")
        @NotNull @Positive Long stepId,
        @Schema(description = "행동 유형", example = "SELECT", allowableValues = {"SELECT", "OPTION", "QUANTITY", "NEXT", "BACK", "HOME", "PAY", "AI_HELP"})
        @NotNull ActionType actionType,
        @Schema(description = "선택한 메뉴·버튼 코드", example = "MENU_AMERICANO")
        @Size(max = 50) String targetCode,
        @Schema(description = "옵션, 수량 등 행동별 추가 정보", example = "{\"quantity\": 1}")
        JsonNode actionData,
        @Schema(description = "브라우저에서 행동이 발생한 시각(ISO 8601, KST 오프셋 포함)", example = "2026-10-06T14:31:12+09:00")
        @NotNull OffsetDateTime occurredAt
) {
}
