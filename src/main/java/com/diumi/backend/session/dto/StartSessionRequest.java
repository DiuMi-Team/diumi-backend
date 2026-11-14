package com.diumi.backend.session.dto;

import com.diumi.backend.session.entity.SessionType;
import jakarta.validation.constraints.NotNull;

public record StartSessionRequest(
        @NotNull Long scenarioId,
        @NotNull SessionType sessionType,
        Long missionId
) {
}
