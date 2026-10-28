package com.diumi.backend.ai.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StartAiConnectionRequest(
        @NotNull @Positive Long stepId
) {
}
