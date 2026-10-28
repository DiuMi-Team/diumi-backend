package com.diumi.backend.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record SaveAiMessagesRequest(
        @NotNull @Positive Long stepId,
        @NotBlank @Size(max = 4000) String question,
        @NotBlank @Size(max = 4000) String answer,
        @PositiveOrZero Integer responseTimeMs
) {
}
