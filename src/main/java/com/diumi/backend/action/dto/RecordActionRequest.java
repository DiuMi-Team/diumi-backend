package com.diumi.backend.action.dto;

import com.diumi.backend.action.entity.ActionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import tools.jackson.databind.JsonNode;

public record RecordActionRequest(
        @NotNull @Positive Long stepId,
        @NotNull ActionType actionType,
        @Size(max = 50) String targetCode,
        JsonNode actionData,
        @NotNull OffsetDateTime occurredAt
) {
}
