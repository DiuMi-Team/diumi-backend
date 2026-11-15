package com.diumi.backend.ai.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;

public record StartAiConnectionRequest(
        @NotNull @Positive Long stepId,
        @Pattern(regexp = "^[A-Z][A-Z0-9_]*$") String screenCode
) {
}
