package com.diumi.backend.result.dto;

import com.diumi.backend.session.entity.SessionStatus;
import jakarta.validation.constraints.NotNull;

public record EndSessionRequest(
        @NotNull SessionStatus status
) {
}
